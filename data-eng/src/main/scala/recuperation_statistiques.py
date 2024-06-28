import csv
import os
from collections import Counter
import matplotlib.pyplot as plt
import numpy as np

# Liste des rues interdites
rues_interdites = ['ISSOU', 'Mickey', 'Lachacnl', 'Liverpool']

# Compteur pour les rues interdites
compteur_rues = Counter()

# Fonction pour lire le CSV et compter les rues interdites
def compter_rues_interdites(nom_fichier_csv):
    with open(nom_fichier_csv, newline='', encoding='utf-8') as csvfile:
        lecteur_csv = csv.reader(csvfile)
        for ligne in lecteur_csv:
            # La rue se trouve à la colonne 6 (index 5 car les index commencent à 0)
            rue = ligne[5]
            if rue in rues_interdites:
                compteur_rues[rue] += 1

# Répertoire contenant les fichiers CSV
repertoire_csv = 'spark_output/batch_output'

# Parcourir tous les fichiers dans le répertoire
for nom_fichier in os.listdir(repertoire_csv):
    if nom_fichier.endswith('.csv'):
        chemin_fichier = os.path.join(repertoire_csv, nom_fichier)
        compter_rues_interdites(chemin_fichier)

# Affichage des résultats
for rue, count in compteur_rues.items():
    print(f"La rue '{rue}' a été fréquentée {count} fois.")

# Création du graphique
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
plt.subplots_adjust(bottom=0.3)

# Trouver les 3 rues les plus fréquentées
rues_et_frequentations = sorted(compteur_rues.items(), key=lambda item: item[1], reverse=True)
top_3_rues = rues_et_frequentations[:3]

# Ajouter une phrase en dessous des 3 rues les plus fréquentées
message = "Attention, renforcer les contrôles de police !"
plt.text(0.5, -0.2, message, ha='center', va='center', transform=plt.gca().transAxes, fontsize=12, color='red')

# Affichage du graphique
plt.show()