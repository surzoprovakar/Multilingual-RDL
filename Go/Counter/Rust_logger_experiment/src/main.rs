// rust/src/main.rs

use prost::Message;
use std::fs::{File, OpenOptions};
use std::io::{self, BufRead, BufReader, Write};
use std::net::{TcpListener, TcpStream};
use std::sync::{Arc, Mutex};
use std::time::{SystemTime, UNIX_EPOCH};

mod logger {
    tonic::include_proto!("logger");
}

use logger::LogMsg;

#[derive(Clone)]
struct LamportClock {
    time: Arc<Mutex<i32>>,
}

impl LamportClock {
    fn new() -> Self {
        LamportClock {
            time: Arc::new(Mutex::new(0)),
        }
    }

    fn increment(&self) {
        let mut time = self.time.lock().unwrap();
        *time += 1;
    }

    fn get_timestamp(&self) -> (i32, String) {
        let time = self.time.lock().unwrap();
        let current_time = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap()
            .as_secs();
        (*time, format!("{}", current_time))
    }
}

fn create_log_file(rid: i32) -> io::Result<()> {
    let log_file = format!("../Replica_{}.log", rid);
    if File::open(&log_file).is_ok() {
        println!("Log File Already Exists");
        return Ok(());
    }
    File::create(log_file)?;
    Ok(())
}

fn persist_log(rid: i32, msg: &str, clock: &LamportClock) -> io::Result<()> {
    let log_file = format!("../Replica_{}.log", rid);
    let mut file = OpenOptions::new().append(true).create(true).open(log_file)?;

    clock.increment();
    let (lamport_time, physical_time) = clock.get_timestamp();
    let log_entry = format!("{}, Lamport Time: {}, Physical Time: {}\n", msg, lamport_time, physical_time);

    file.write_all(log_entry.as_bytes())?;
    Ok(())
}

fn handle_client(mut stream: TcpStream, clock: LamportClock) {
    let mut buffer = Vec::new();
    let mut reader = BufReader::new(&stream);

    while reader.read_until(0x00, &mut buffer).unwrap() > 0 {
        let log_msg = LogMsg::decode(&buffer[..buffer.len() - 1]).unwrap();
        let rid = log_msg.id;
        let log_message = log_msg.logs;

        if log_message == "create" {
            create_log_file(rid).unwrap();
        } else {
            persist_log(rid, &log_message, &clock).unwrap();
        }
        buffer.clear();
    }
}

fn main() -> io::Result<()> {
    let listener = TcpListener::bind("127.0.0.1:8080")?;
    let clock = LamportClock::new();

    println!("Logger server started on localhost:8080");

    for stream in listener.incoming() {
        match stream {
            Ok(stream) => {
                let clock = clock.clone();
                std::thread::spawn(move || {
                    handle_client(stream, clock);
                });
            }
            Err(e) => eprintln!("Connection failed: {}", e),
        }
    }
    Ok(())
}
