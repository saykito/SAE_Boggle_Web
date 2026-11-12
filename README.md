# SAE Boggle, partie web

SAE S3 BUT Informatique, IUT de Marne-la-Vallée (2026 - 2027).

Partie web d'une plateforme de Boggle en ligne. Le projet complet comprend un moteur de jeu en C (génération de grilles, recherche de mots dans un dictionnaire), une base de données et ce site.

## Fonctionnalités

- Création de compte et connexion
- Mot de passe oublié : lien de réinitialisation envoyé par mail (PHPMailer)
- Liste des parties et page de partie

## Technologies

- PHP sans framework, PDO
- PostgreSQL (Neon en production)
- Docker, déploiement sur Render

## Lancer en local

1. Créer la base `boggle` dans PostgreSQL et exécuter `bdd.sql`.
2. Mettre le mot de passe PostgreSQL dans `config.php`.
3. Dans le dossier du projet : `php -S localhost:8000`, puis ouvrir http://localhost:8000.

## En ligne

Hébergé sur Render (Dockerfile), base PostgreSQL sur Neon.
Variables d'environnement : `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASS`.
