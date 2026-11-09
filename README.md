# SAE Boggle, partie web

SAE S3 BUT Informatique, IUT de Marne-la-Vallée (2026 - 2027).
Site PHP sans framework, base PostgreSQL : création de compte, connexion, récupération du mot de passe, liste des parties.

## Lancer en local

1. Créer la base `boggle` dans PostgreSQL et exécuter `bdd.sql`.
2. Mettre le mot de passe PostgreSQL dans `config.php`.
3. Dans le dossier du projet : `php -S localhost:8000`, puis ouvrir http://localhost:8000.

## En ligne

Hébergé sur Render (Dockerfile), base PostgreSQL sur Neon.
Variables d'environnement : `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASS`.

Mot de passe oublié : le lien est envoyé par mail avec PHPMailer (dossier `PHPMailer/`).
