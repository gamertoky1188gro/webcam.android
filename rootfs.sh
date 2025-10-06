#!/bin/bash
# Download rootfs.img from MEGA using megatools

# Ensure megatools is installed
if ! command -v megadl &> /dev/null; then
    echo "megadl not found. Installing..."
    sudo apt update && sudo apt install -y megatools
fi

# Combined MEGA URL with decryption key
MEGA_URL="https://mega.nz/file/SIU3lR6B#K7ZwV_WPvraLVgAAy6FSspRZ1tKNGMfb_z6UMzCkIOQ"

# Download the file
echo "Downloading rootfs.img from MEGA..."
megadl "$MEGA_URL"

echo "Download complete!"
