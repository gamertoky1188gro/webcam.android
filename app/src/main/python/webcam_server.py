import asyncio
import cv2
import websockets
import time

VIDEO_PATH = "C:/Users/Cyber Code Master/PycharmProjects/WebVirtualCam/v.mp4"

async def send_frames(websocket, _):
    cap = cv2.VideoCapture(VIDEO_PATH)

    # Get original FPS and resolution
    video_fps = cap.get(cv2.CAP_PROP_FPS)
    width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
    print(f"Video FPS: {video_fps}, Resolution: {width}x{height}")

    DELAY = 1 / video_fps if video_fps > 0 else 1 / 30  # fallback to 30 FPS if unknown

    while cap.isOpened():
        start = time.time()
        ret, frame = cap.read()
        if not ret:
            break

        # Use native resolution (no resize)
        # frame = cv2.resize(frame, (width, height))  # Not needed, frame already native size

        _, buffer = cv2.imencode('.jpg', frame)
        try:
            await websocket.send(buffer.tobytes())  # Send as binary
        except Exception as e:
            print("WebSocket error:", e)
            break

        elapsed = time.time() - start
        await asyncio.sleep(max(0, DELAY - elapsed))

    cap.release()

async def main():
    async with websockets.serve(send_frames, "localhost", 8765):
        print("Server running at ws://localhost:8765")
        await asyncio.Future()  # Run forever

asyncio.run(main())

