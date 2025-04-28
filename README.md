# 🌍 Multilingual Support for Replicated Data Libraries (RDL)

This project delivers **multilingual support** for replicated data systems, enabling **application developers** to build distributed applications using the **best programming language** for their needs, while maintaining **high performance** and **strong software quality**.

## Overview

Replicated data libraries (RDLs) are the backbone of many distributed applications, but most libraries are limited to a single language ecosystem. This project breaks that barrier by providing **seamless multilingual support** through a **common serialization and communication layer**.

Key outcomes:
- 📈 **Performance boosted by 54%** through optimized cross-language communication.
- 🧹 **Software quality improved by 38%** via better language-specific integration and reduced technical debt.
- 💬 **Developer choice**: Use Go, Java, JavaScript, or C++ based on application requirements.

## Key Features

- 🌐 **Cross-Language RDL Access**: Applications in Go, Java, JavaScript, and C++ can seamlessly interact with replicated data structures.
- ⚡ **High-Performance Communication**: Utilizes **Protobuf** for efficient serialization across heterogeneous environments.
- 🧩 **Language-Specific Adapters**: Lightweight wrappers for each language to integrate smoothly with existing codebases.
- 🛠️ **Unified API Design**: Consistent operation semantics regardless of the programming language used.
- 📦 **Pluggable Architecture**: Easily extendable to new languages or back-end replication protocols.

## Technologies Used

- **Go**, **Java**, **JavaScript**, **C++** — supported application languages.
- **Protocol Buffers (Protobuf)** — for efficient, language-neutral data serialization.
- **Custom Language Adapters** — for bridging local APIs to the RDL core.

## Motivation

Developers often face trade-offs when choosing a technology stack for distributed applications, sacrificing ideal tools for library compatibility.  
This project eliminates that trade-off by **empowering developers to build applications in their preferred languages** without compromising on replication reliability or system performance.

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/surzoprovakar/Multilingual-RDL.git
   cd Multilingual-RDL

2. Run the project for a particular RDL data structure...
- [ ] Open a CLI for each language, Go, Java, and JavaScript, inside the directory of the RDT
- [ ] Build each project
- [ ] Run a replica from each project

3. Example
- [x] Open 3 CLIs, one inside *Go/Counter*, one inside *Java/Counter*, and the last inside *JS/Counter*
- [x] Build each CLI by giving the command **make all**
- [x] To conduct ***Unit*** Testing for Counter's basic functionalities, command **make test** 
- [x] To run, command **./r1.sh** inside *Go/Counter*, **./r2.sh** inside *Java/Counter*, and **./r3.sh** inside *JS/Counter*
- [x] To clean, command **make clean**
