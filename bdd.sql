-- Version PostgreSQL
-- À exécuter dans pgAdmin (Query Tool) sur la base "boggle"

CREATE TABLE utilisateurs (
    id SERIAL PRIMARY KEY,
    pseudo VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    mdp VARCHAR(255) NOT NULL,              -- hash, jamais le mot de passe en clair
    token_recup VARCHAR(64) DEFAULT NULL,   -- pour "mot de passe oublié"
    token_expire TIMESTAMP DEFAULT NULL
);

CREATE TABLE parties (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    createur_id INT NOT NULL REFERENCES utilisateurs(id),
    date_creation TIMESTAMP NOT NULL
);
