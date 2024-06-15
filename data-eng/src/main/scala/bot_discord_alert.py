import discord
import os
import time
import asyncio
import logging

from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler

TOKEN = 'MTI1MTIwODQ3MTE2OTMzOTQyMg.GixiIs.3jzanpEWX0RrMTzEKxIXOYDxAkF_d_pyVrDKo8'
CHANNEL_ID = 1251211675974242367  # Replace with your channel ID

# Configure logging
logging.basicConfig(level=logging.INFO)

intents = discord.Intents.default()
intents.message_content = True  # Make sure to enable message content intent

client = discord.Client(intents=intents)

# Function to split the message into chunks of 2000 characters or fewer
def split_message(message, chunk_size=2000):
    return [message[i:i + chunk_size] for i in range(0, len(message), chunk_size)]

class NewFileHandler(FileSystemEventHandler):
    def __init__(self, channel):
        self.channel = channel

    def on_created(self, event):
        logging.info(f"File created: {event.src_path}")
        if event.is_directory:
            return
        elif event.src_path.endswith('.csv'):
            logging.info(f"Processing new CSV file: {event.src_path}")
            asyncio.run_coroutine_threadsafe(self.process_new_file(event.src_path), client.loop)

    async def process_new_file(self, file_path):
        try:
            with open(file_path, 'r') as f:
                buffer = f.read()
                message_parts = split_message(buffer)
                for part in message_parts:
                    await self.channel.send(part)
                    logging.info(f"Sent part of the file: {file_path}")
        except Exception as e:
            logging.error(f"Error processing file {file_path}: {e}")

@client.event
async def on_ready():
    logging.info(f'We have logged in as {client.user}')
    channel = client.get_channel(CHANNEL_ID)
    if channel is None:
        logging.error(f"Could not find channel with ID {CHANNEL_ID}")
        return
    event_handler = NewFileHandler(channel)
    observer = Observer()
    observer.schedule(event_handler, path='./output/', recursive=False)
    observer.start()
    try:
        while True:
            await asyncio.sleep(1)
    except KeyboardInterrupt:
        observer.stop()
    observer.join()

client.run(TOKEN)
