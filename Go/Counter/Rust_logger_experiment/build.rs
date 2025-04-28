// rust/build.rs
fn main() {
    tonic_build::compile_protos("src/proto/logger.proto")
        .expect("Failed to compile protos");
}
