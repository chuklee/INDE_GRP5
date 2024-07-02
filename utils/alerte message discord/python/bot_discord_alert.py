import discord
import asyncpg
import asyncio
import logging

TOKEN = 'MTI1MTIwODQ3MTE2OTMzOTQyMg.GixiIs.3jzanpEWX0RrMTzEKxIXOYDxAkF_d_pyVrDKo8'
CHANNEL_ID = 1251211675974242367
DATABASE_URL = 'postgresql://postgres:abc@localhost:5432/postgres'  # Your PostgreSQL connection string
# Configure logging
logging.basicConfig(level=logging.INFO)

intents = discord.Intents.default()
intents.message_content = True  # Make sure to enable message content intent

client = discord.Client(intents=intents)

async def send_discord_message(channel, message):
    await channel.send(message)

async def listen_to_postgres(channel):
    conn = await asyncpg.connect(DATABASE_URL)
    await conn.add_listener('new_insert', lambda conn, pid, channel_name, payload: asyncio.create_task(process_notification(channel, payload)))
    while True:
        await asyncio.sleep(1)

async def process_notification(channel, payload):
    logging.info(f"Notification received: {payload}")
    await send_discord_message(channel, f"ALERT ALERT ALERT: {payload} please get the f** out of there!")

@client.event
async def on_ready():
    logging.info(f'We have logged in as {client.user}')
    channel = client.get_channel(CHANNEL_ID)
    if channel is None:
        logging.error(f"Could not find channel with ID {CHANNEL_ID}")
        return
    asyncio.create_task(listen_to_postgres(channel))

client.run(TOKEN)
