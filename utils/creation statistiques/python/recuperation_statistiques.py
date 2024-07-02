import os
import matplotlib.pyplot as plt
import numpy as np
import csv
from collections import Counter

# Lire les résultats des rues interdites
def lire_compteur_rues(nom_fichier_csv):
    compteur_rues = Counter()
    with open(nom_fichier_csv, newline='', encoding='utf-8') as csvfile:
        lecteur_csv = csv.DictReader(csvfile)
        for ligne in lecteur_csv:
            rue = ligne['rue']
            count = int(ligne['count'])
            compteur_rues[rue] += count
    return compteur_rues

# Lire les résultats des âges
def lire_compteur_ages(nom_fichier_csv):
    compteur_ages = Counter()
    with open(nom_fichier_csv, newline='', encoding='utf-8') as csvfile:
        lecteur_csv = csv.DictReader(csvfile)
        for ligne in lecteur_csv:
            intervalle_age = int(ligne['intervalle_age'])
            count = int(ligne['count'])
            compteur_ages[intervalle_age] += count
    return compteur_ages

# Lire les résultats des métiers
def lire_compteur_jobs(nom_fichier_csv):
    compteur_jobs = Counter()
    with open(nom_fichier_csv, newline='', encoding='utf-8') as csvfile:
        lecteur_csv = csv.DictReader(csvfile)
        for ligne in lecteur_csv:
            job = ligne['job']
            count = int(ligne['count'])
            compteur_jobs[job] += count
    return compteur_jobs
# Lire les résultats des heures
def lire_compteur_heures(nom_fichier_csv):
    compteur_heures = Counter()
    with open(nom_fichier_csv, newline='', encoding='utf-8') as csvfile:
        lecteur_csv = csv.DictReader(csvfile)
        for ligne in lecteur_csv:
            heure = int(ligne['heure'])
            count = int(ligne['count'])
            compteur_heures[heure] += count
    return compteur_heures
# Chemins vers les fichiers CSV générés par Spark
chemin_compteur_rues = 'input_python/compteur_rues'
chemin_compteur_ages = 'input_python/compteur_ages'
chemin_compteur_jobs = 'input_python/compteur_jobs'
chemin_compteur_heures = 'input_python/compteur_heures'

# Combiner les fichiers CSV en un seul pour chaque compteur
fichiers_rues = [os.path.join(chemin_compteur_rues, f) for f in os.listdir(chemin_compteur_rues) if f.endswith('.csv')]
fichiers_ages = [os.path.join(chemin_compteur_ages, f) for f in os.listdir(chemin_compteur_ages) if f.endswith('.csv')]
fichiers_jobs = [os.path.join(chemin_compteur_jobs, f) for f in os.listdir(chemin_compteur_jobs) if f.endswith('.csv')]
fichiers_heures = [os.path.join(chemin_compteur_heures, f) for f in os.listdir(chemin_compteur_heures) if f.endswith('.csv')]

compteur_rues = Counter()
for fichier in fichiers_rues:
    compteur_rues.update(lire_compteur_rues(fichier))

compteur_ages = Counter()
for fichier in fichiers_ages:
    compteur_ages.update(lire_compteur_ages(fichier))

compteur_jobs = Counter()
for fichier in fichiers_jobs:
    compteur_jobs.update(lire_compteur_jobs(fichier))
compteur_heures = Counter()
for fichier in fichiers_heures:
    compteur_heures.update(lire_compteur_heures(fichier))
# Affichage des résultats pour les rues interdites
for rue, count in compteur_rues.items():
    print(f"La rue '{rue}' a été fréquentée {count} fois.")

# Création du graphique des rues interdites
rues = list(compteur_rues.keys())
frequentations = list(compteur_rues.values())

couleurs = plt.cm.tab20(np.linspace(0, 1, len(rues)))

plt.figure(figsize=(10, 6))
bars = plt.bar(rues, frequentations, color=couleurs)
plt.xlabel('Rues Interdites', labelpad=20)
plt.ylabel('Nombre de Fréquentations', labelpad=20)
plt.title('Fréquentation des Rues Interdites')
plt.xticks(rotation=45)

max_frequentations = max(frequentations) if frequentations else 1
plt.yticks(range(0, max_frequentations + 1))

plt.grid(axis='y', linestyle='--', alpha=0.7)
plt.tight_layout()
plt.subplots_adjust(bottom=0.35)

message = "Attention, renforcer les contrôles de police !"
plt.text(0.5, -0.3, message, ha='center', va='center', transform=plt.gca().transAxes, fontsize=12, color='red')

plt.show()

# Affichage des résultats pour les âges
for intervalle, count in compteur_ages.items():
    print(f"Les personnes de {intervalle} à {intervalle+9} ans ont été rencontrées {count} fois dans les rues interdites.")

# Création du graphique des âges
intervalles = list(compteur_ages.keys())
frequentations_ages = list(compteur_ages.values())

intervalles.sort()

couleurs_ages = plt.cm.tab20(np.linspace(0, 1, len(intervalles)))

plt.figure(figsize=(10, 6))
bars = plt.bar(intervalles, frequentations_ages, width=8, align='edge', color=couleurs_ages)
plt.xlabel('Tranches d\'âges (années)', labelpad=20)
plt.ylabel('Nombre de Fréquentations', labelpad=20)
plt.title('Fréquentation des Rues Interdites par Tranche d\'Âge')
plt.xticks(intervalles, [f'{i}-{i+9} ans' for i in intervalles], rotation=45)

max_frequentations_ages = max(frequentations_ages) if frequentations_ages else 1
plt.yticks(range(0, max_frequentations_ages + 1))

plt.grid(axis='y', linestyle='--', alpha=0.7)
plt.tight_layout()
plt.subplots_adjust(bottom=0.35)

message_ages = "Augmentation de l'impôt sur le revenu pour les tranches d'âges réfractaires fortement conseillé."
plt.text(0.5, -0.4, message_ages, ha='center', va='center', transform=plt.gca().transAxes, fontsize=12, color='red')

plt.show()
# Affichage des résultats pour les métiers
for job, count in compteur_jobs.items():
    print(f"Le métier '{job}' a été rencontré {count} fois dans les rues interdites.")

# Création du graphique des métiers
jobs = list(compteur_jobs.keys())
frequentations_jobs = list(compteur_jobs.values())

couleurs_jobs = plt.cm.tab20(np.linspace(0, 1, len(jobs)))

plt.figure(figsize=(10, 6))
bars = plt.bar(jobs, frequentations_jobs, color=couleurs_jobs)
plt.xlabel('Métiers', labelpad=20)
plt.ylabel('Nombre de Fréquentations', labelpad=20)
plt.title('Fréquentation des Rues Interdites par Métiers')
plt.xticks(rotation=45)

max_frequentations_jobs = max(frequentations_jobs) if frequentations_jobs else 1
plt.yticks(range(0, max_frequentations_jobs + 1))

plt.grid(axis='y', linestyle='--', alpha=0.7)
plt.tight_layout()
plt.subplots_adjust(bottom=0.35)

message_jobs = "Attention aux métiers réfractaires - Réduction des salaires de ces professions conseillé !"
plt.text(0.5, -0.4, message_jobs, ha='center', va='center', transform=plt.gca().transAxes, fontsize=12, color='red')

plt.show()
# Affichage des résultats pour les heures
for heure, count in compteur_heures.items():
    print(f"À l'heure '{heure}h', il y a eu {count} présences dans les rues interdites.")

# Création du graphique des heures
heures = list(compteur_heures.keys())
frequentations_heures = list(compteur_heures.values())

heures.sort()

couleurs_heures = plt.cm.tab20(np.linspace(0, 1, len(heures)))

plt.figure(figsize=(10, 6))
bars = plt.bar(heures, frequentations_heures, width=0.8, align='center', color=couleurs_heures)
plt.xlabel('Heures', labelpad=20)
plt.ylabel('Nombre de Présences', labelpad=20)
plt.title('Présences dans les Rues Interdites par Heure')
plt.xticks(heures, [f'{h}h' for h in heures], rotation=45)

max_frequentations_heures = max(frequentations_heures) if frequentations_heures else 1
plt.yticks(range(0, max_frequentations_heures + 1))

plt.grid(axis='y', linestyle='--', alpha=0.7)
plt.tight_layout()
plt.subplots_adjust(bottom=0.35)

message_heures = "Heures de fréquentations maximales dans les zones interdites, renforcer les contrôles !"
plt.text(0.5, -0.3, message_heures, ha='center', va='center', transform=plt.gca().transAxes, fontsize=12, color='red')

plt.show()