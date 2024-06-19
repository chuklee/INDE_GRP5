"""
Dans ce fichier, le but est de créer une demande de renseignements que l'Etat exige.

"""

import os
import time


# Global variable to keep track of all files that have been processed
processed_files = set()

while True:
    # Ask the user for input to continue or exit the program
    user_input = input("Press 'q' to quit.\nOr press any other key to get the report of all users in the forbidden areas: ")
    if user_input == 'q':
        print("Exiting the program...")
        break
    else:
        print("Checking for new files...")
        files = os.listdir('./output/')
        csv_files = [f for f in files if f.endswith('.csv')]
        report  = ""
        for file in csv_files:
            if file not in processed_files:
                processed_files.add(file)
                message = ''
                with open(os.path.join('./output/', file), 'r') as f:
                    buffer = f.read()
                    message += buffer + "\n"
                report += message
        print(report)