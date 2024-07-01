import os
import csv
from collections import Counter
import matplotlib.pyplot as plt
import numpy as np
import psycopg2

# Fonction pour récupérer les zones interdites depuis la base de données
def recuperer_zones_interdites():
    conn_params = {
        'dbname': 'postgres',
        'user': 'postgres',
        'password': 'abc',
        'host': '172.28.85.10',
        'port': '5432'
    }

    try:
        conn = psycopg2.connect(**conn_params)
        cursor = conn.cursor()
        cursor.execute("SELECT area FROM forbidden_areas;")
        rows = cursor.fetchall()
        return [row[0] for row in rows]
    except Exception as e:
        print(f"Erreur lors de la connexion ou de la récupération des données: {e}")
        return []
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Liste des rues interdites récupérées de la base de données
rues_interdites = recuperer_zones_interdites()

# Compteur pour les rues interdites
compteur_rues = Counter()

# Compteur pour les âges
compteur_ages = Counter()

# Fonction pour lire le CSV et compter les rues interdites et les âges
def compter_rues_et_ages_interdits(nom_fichier_csv):
    with open(nom_fichier_csv, newline='', encoding='utf-8') as csvfile:
        lecteur_csv = csv.reader(csvfile)
        for ligne in lecteur_csv:
            rue = ligne[5]
            age = int(ligne[7])
            if rue in rues_interdites:
                compteur_rues[rue] += 1
                # Compter l'âge dans l'intervalle approprié
                intervalle_age = (age // 10) * 10
                compteur_ages[intervalle_age] += 1

# Répertoire contenant les fichiers CSV
repertoire_csv = 'spark_output/batch_output'

# Parcourir tous les fichiers dans le répertoire
for nom_fichier in os.listdir(repertoire_csv):
    if nom_fichier.endswith('.csv'):
        chemin_fichier = os.path.join(repertoire_csv, nom_fichier)
        compter_rues_et_ages_interdits(chemin_fichier)

# Affichage des résultats pour les rues interdites
for rue, count in compteur_rues.items():
    print(f"La rue '{rue}' a été fréquentée {count} fois.")

# Création du graphique des rues interdites
rues = list(compteur_rues.keys())
frequentations = list(compteur_rues.values())

# Définir une liste de couleurs différentes
couleurs = plt.cm.tab20(np.linspace(0, 1, len(rues)))

plt.figure(figsize=(10, 6))
bars = plt.bar(rues, frequentations, color=couleurs)
plt.xlabel('Rues Interdites', labelpad=20)
plt.ylabel('Nombre de Fréquentations', labelpad=20)
plt.title('Fréquentation des Rues Interdites')
plt.xticks(rotation=45)

# Définir l'échelle des ordonnées pour utiliser uniquement des entiers
max_frequentations = max(frequentations) if frequentations else 1
plt.yticks(range(0, max_frequentations + 1))

plt.grid(axis='y', linestyle='--', alpha=0.7)
plt.tight_layout()

# Ajuster la position des sous-plots pour éviter le chevauchement
plt.subplots_adjust(bottom=0.35)

# Trouver les 3 rues les plus fréquentées
rues_et_frequentations = sorted(compteur_rues.items(), key=lambda item: item[1], reverse=True)
top_3_rues = rues_et_frequentations[:3]

# Ajouter une phrase en dessous des 3 rues les plus fréquentées
message = "Attention, renforcer les contrôles de police !"
plt.text(0.5, -0.3, message, ha='center', va='center', transform=plt.gca().transAxes, fontsize=12, color='red')

# Affichage du graphique
plt.show()

# Affichage des résultats pour les âges
for intervalle, count in compteur_ages.items():
    print(f"Les personnes de {intervalle} à {intervalle+9} ans ont été rencontrées {count} fois dans les rues interdites.")

# Création du graphique des âges
intervalles = list(compteur_ages.keys())
frequentations_ages = list(compteur_ages.values())

# Ordonner les intervalles
intervalles.sort()

# Définir une liste de couleurs différentes
couleurs_ages = plt.cm.tab20(np.linspace(0, 1, len(intervalles)))

plt.figure(figsize=(10, 6))
bars = plt.bar(intervalles, frequentations_ages, width=8, align='edge', color=couleurs_ages)
plt.xlabel('Tranches d\'âges (années)', labelpad=20)
plt.ylabel('Nombre de Fréquentations', labelpad=20)
plt.title('Fréquentation des Rues Interdites par Tranche d\'Âge')
plt.xticks(intervalles, [f'{i}-{i+9} ans' for i in intervalles], rotation=45)

# Définir l'échelle des ordonnées pour utiliser uniquement des entiers
max_frequentations_ages = max(frequentations_ages) if frequentations_ages else 1
plt.yticks(range(0, max_frequentations_ages + 1))

plt.grid(axis='y', linestyle='--', alpha=0.7)
plt.tight_layout()
plt.subplots_adjust(bottom=0.35)

# Ajouter une phrase en dessous du graphique
message_ages = "Augmentation de l'impôt sur le revenu pour les tranches d'âges réfractaires fortement conseillé."
plt.text(0.5, -0.4, message_ages, ha='center', va='center', transform=plt.gca().transAxes, fontsize=12, color='red')

# Affichage du graphique
plt.show()