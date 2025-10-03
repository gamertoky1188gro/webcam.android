from adb_shell.adb_device import AdbDeviceTcp
from adb_shell.auth.sign_pythonrsa import PythonRSASigner
import socket

def main():
    # Load private/public key
    adbkey = "/data/data/cyber.code.master.webcam/code_cache/adbkey"
    with open(adbkey) as f:
        priv = f.read()
    with open(adbkey + ".pub") as f:
        pub = f.read()

    signer = PythonRSASigner(pub, priv)

    # Always loopback, because phone talks to its own adbd
    ip = "127.0.0.1"
    port_range = range(30000, 50000)  # wireless debugging range

    device = None
    for port in port_range:
        try:
            print(f"[*] Checking port {port}")
            candidate = AdbDeviceTcp(ip, port, default_transport_timeout_s=3.0)
            candidate.connect(rsa_keys=[signer], auth_timeout_s=0.5)
            print(f"[+] Connected successfully on port {port}")
            device = candidate
            break
        except Exception as e:
            # Keep scanning silently or print error for debugging
            # print(f"[-] Port {port} failed: {e}")
            continue

    if device is None:
        print("[-] Could not find an open ADB port.")
    else:
        try:
            response = device.shell("echo HELLO_FROM_APP")
            print("Device says:", response.strip())
        except Exception as e:
            print("[-] Connected but command failed:", e)
