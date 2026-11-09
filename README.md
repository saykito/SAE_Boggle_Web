# SAE Boggle, partie web

SAE S3 du BUT Informatique, IUT de Marne-la-Vallée, Université Gustave Eiffel (2026 - 2027).

Partie web de la plateforme Boggle en ligne : site PHP sans framework avec une base PostgreSQL.
Projet en cours de développement.

## Fonctionnalités

- Création de compte et connexion (mots de passe hachés avec `password_hash()`)
- Mot de passe oublié : lien de réinitialisation envoyé par mail (PHPMailer), avec jeton à durée limitée
- Liste des parties et page de détail d'une partie
- Protection XSS des affichages (`htmlspecialchars`) et cookie de session `httponly` / `SameSite=Lax`

## Technologies

PHP 8.3, PostgreSQL, PDO, PHPMailer, Docker (hébergement Render), Neon (PostgreSQL en ligne).

## Structure

```
.
├── config.php            Connexion PDO, session, fonctions communes
├── bdd.sql               Création des tables (utilisateurs, parties)
├── index.php             Connexion
├── inscription.php       Création de compte
├── recuperation.php      Demande de réinitialisation du mot de passe
├── reinitialisation.php  Choix du nouveau mot de passe
├── mail.php              Envoi des mails (PHPMailer)
├── parties.php           Liste des parties
├── partie.php            Détail d'une partie
├── deconnexion.php
├── style.css
├── PHPMailer/            Bibliothèque PHPMailer (licence dans PHPMailer/LICENSE)
└── Dockerfile            Image php:8.3-apache utilisée sur Render
```

## Lancer en local

1. Créer la base `boggle` dans PostgreSQL et exécuter `bdd.sql`.
2. Mettre le mot de passe PostgreSQL dans `config.php` (valeur par défaut de `DB_PASS`), sans le commiter.
3. Dans le dossier du projet : `php -S localhost:8000`, puis ouvrir http://localhost:8000.

## En ligne

Hébergé sur Render (Dockerfile), base PostgreSQL sur Neon.

| Variable | Rôle |
| --- | --- |
| `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASS` | Connexion à la base (SSL activé automatiquement) |
| `SMTP_HOST`, `SMTP_PORT` | Serveur d'envoi des mails (port 587 par défaut) |
| `SMTP_USER`, `SMTP_PASS` | Identifiants SMTP |
| `SMTP_FROM` | Adresse d'expéditeur (`noreply@boggle.fr` par défaut) |

## Auteur

Yasin Boukraa, BUT Informatique 2e année.
