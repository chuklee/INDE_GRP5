import discord
import os
import time
import asyncio

TOKEN = 'MTI1MTIwODQ3MTE2OTMzOTQyMg.GixiIs.3jzanpEWX0RrMTzEKxIXOYDxAkF_d_pyVrDKo8'
CHANNEL_ID = 1251211675974242367  # Replace with your channel ID


intents = discord.Intents.default()
intents.message_content = True  # Make sure to enable message content intent

client = discord.Client(intents=intents)

# Function to split the message into chunks of 1000 characters or fewer
def split_message(message, chunk_size=1000):
    return [message[i:i + chunk_size] for i in range(0, len(message), chunk_size)]

# Global variable to keep track of all files that have been processed
processed_files = set()

async def check_for_new_files():
    await client.wait_until_ready()
    channel = client.get_channel(CHANNEL_ID)
    while not client.is_closed():
        files = os.listdir('./output/')
        csv_files = [f for f in files if f.endswith('.csv')]
        for file in csv_files:
            if file not in processed_files:
                processed_files.add(file)
                message = ''
                with open(os.path.join('./output/', file), 'r') as f:
                    buffer = f.read()
                    message += buffer + "\n"  # Add a newline between files for better readability
                message_parts = split_message(message)
                for part in message_parts:
                    await channel.send(part)
        await asyncio.sleep(20)  # Check for new files every 20 seconds

@client.event
async def on_ready():
    print(f'We have logged in as {client.user}')

@client.event
async def setup_hook():
    client.loop.create_task(check_for_new_files())

client.run(TOKEN)
