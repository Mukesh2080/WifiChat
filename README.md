# 📡 WifiChat - Peer-to-Peer Local Chat App

WifiChat is a lightweight, offline, peer-to-peer chat application for Android that lets users communicate securely over the **same Wi-Fi network** — no internet or central server required.

<img width="200" height="200" alt="app icon for wificha" src="https://github.com/user-attachments/assets/1e0d4bc3-4645-4a2d-8eee-145667713153" />

---

## 🚀 Features


- 📶 **Peer Discovery**: Discover nearby users automatically on the same Wi-Fi network.
- 🔐 **Secure Communication**: All messages are end-to-end encrypted.
- 💬 **Real-time Messaging**: Seamless text messaging with instant delivery.
- 🔄 **Delivery & Read Receipts**: Know when your message is sent, delivered, and read.
- 📁 **Message Caching**: Works even if a peer temporarily disconnects.
- 👥 **Group Chat Support** *(coming soon)*.
- 🗂 **No Server Required**: Fully decentralized communication model.
- 🔌 **LAN-Only Communication**: No mobile data or internet needed.

---

## 📷 Screenshots

| Chat Screen | Peer List | Message Bubble |
|-------------|-----------|----------------|
| ![Chat](screenshots/chat.png) | ![Peers](screenshots/peers.png) | ![Bubble](screenshots/message.png) |

---

## 🧱 Architecture

- **Language**: Java (100%)
- **Networking**: Custom socket-based communication over LAN
- **Discovery**: UDP broadcast for peer finding
- **Encryption**: AES-256 based message encryption
- **Persistence**: Local message caching using Room DB *(optional)*
- **UI**: XML-based custom Android UI with RecyclerView & ViewModel

---

## 🔧 Installation & Setup

1. **Clone the repo**

```bash
git clone https://github.com/Mukesh2080/WifiChat.git
cd WifiChat
