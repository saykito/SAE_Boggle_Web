-- =====================================================================
-- SAE Boggle, module L2 : base de données PostgreSQL
-- Yasin Boukraa, BUT Informatique 2e année
-- Version de travail (environ 60 % du module)
-- =====================================================================


-- ---------------------------------------------------------------------
-- 0. Préparation
-- ---------------------------------------------------------------------

-- On supprime les objets s'ils existent déjà, pour pouvoir relancer
-- le script autant de fois qu'on veut.
DROP VIEW  IF EXISTS profil_public, profil_prive;
DROP TABLE IF EXISTS participer, partie, profil, utilisateur CASCADE;

-- Extension fournie avec PostgreSQL : elle donne la fonction crypt()
-- qui sert à hacher les mots de passe.
CREATE EXTENSION IF NOT EXISTS pgcrypto;


-- ---------------------------------------------------------------------
-- 1. Les utilisateurs
-- ---------------------------------------------------------------------

CREATE TABLE utilisateur (
    id_utilisateur     SERIAL       PRIMARY KEY,
    pseudo             VARCHAR(30)  NOT NULL UNIQUE,
    email              VARCHAR(100) NOT NULL UNIQUE,
    mot_de_passe_hash  VARCHAR(255) NOT NULL,   -- jamais le mot de passe en clair
    date_inscription   TIMESTAMP    NOT NULL DEFAULT NOW(),
    derniere_connexion TIMESTAMP,
    est_actif          BOOLEAN      NOT NULL DEFAULT TRUE
);


-- ---------------------------------------------------------------------
-- 2. Les profils (association POSSEDER : un utilisateur, un profil)
-- ---------------------------------------------------------------------

CREATE TABLE profil (
    id_utilisateur INT PRIMARY KEY REFERENCES utilisateur(id_utilisateur),
    nom            VARCHAR(50),
    prenom         VARCHAR(50),
    avatar_url     VARCHAR(255),
    biographie     TEXT,
    pays           VARCHAR(50),
    est_public     BOOLEAN NOT NULL DEFAULT TRUE
);

-- Profil public : ce que les autres joueurs ont le droit de voir.
-- Pas d'email, pas de nom ni de prénom.
CREATE VIEW profil_public AS
SELECT u.pseudo, p.avatar_url, p.biographie, p.pays
FROM utilisateur u
JOIN profil p ON p.id_utilisateur = u.id_utilisateur
WHERE p.est_public = TRUE;

-- Profil privé : ce que le joueur voit sur sa propre page.
CREATE VIEW profil_prive AS
SELECT u.pseudo, u.email, p.avatar_url, p.nom, p.prenom,
       p.biographie, p.pays, p.est_public
FROM utilisateur u
JOIN profil p ON p.id_utilisateur = u.id_utilisateur;


-- ---------------------------------------------------------------------
-- 3. Les parties (déroulé complet dans une colonne JSONB)
-- ---------------------------------------------------------------------

CREATE TABLE partie (
    id_partie  SERIAL PRIMARY KEY,
    date_debut TIMESTAMP,
    date_fin   TIMESTAMP,
    statut     VARCHAR(20) NOT NULL DEFAULT 'en_attente'
               CHECK (statut IN ('en_attente', 'en_cours', 'terminee', 'annulee')),
    donnees    JSONB NOT NULL   -- grille, joueurs, mots trouvés, scores
);

-- Association PARTICIPER : quel joueur a joué quelle partie.
-- Elle garantit qu'un joueur d'une partie existe vraiment dans la base.
CREATE TABLE participer (
    id_utilisateur INT NOT NULL REFERENCES utilisateur(id_utilisateur),
    id_partie      INT NOT NULL REFERENCES partie(id_partie),
    date_arrivee   TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id_utilisateur, id_partie)
);


-- ---------------------------------------------------------------------
-- 4. Données de test
-- ---------------------------------------------------------------------

-- Mots de passe de test : yasin123, lea123, karim123.
-- crypt(..., gen_salt('bf', 10)) les hache en bcrypt, le même format que
-- password_hash() en PHP.
INSERT INTO utilisateur (pseudo, email, mot_de_passe_hash) VALUES
('yasin', 'yasin@test.fr', crypt('yasin123', gen_salt('bf', 10))),
('lea',   'lea@test.fr',   crypt('lea123',   gen_salt('bf', 10))),
('karim', 'karim@test.fr', crypt('karim123', gen_salt('bf', 10)));

INSERT INTO profil (id_utilisateur, prenom, avatar_url, biographie, pays, est_public) VALUES
(1, 'Yasin', 'avatars/yasin.png', 'Fan de jeux de lettres', 'France', TRUE),
(2, 'Léa',   'avatars/lea.png',   'Je joue le soir',        'France', FALSE),
(3, 'Karim', 'avatars/karim.png', NULL,                     'Maroc',  TRUE);

-- Partie 1 : yasin contre lea, sur la grille d'exemple du sujet.
-- Le mot SIENS est refusé (il repasse par la même case S).
INSERT INTO partie (date_debut, date_fin, statut, donnees) VALUES
('2026-11-18 14:00', '2026-11-18 14:03', 'terminee',
'{
  "grille": {
    "lignes": 4,
    "colonnes": 4,
    "lettres": [["E","U","E","S"], ["O","I","N","I"], ["I","J","E","L"], ["M","I","A","E"]]
  },
  "joueurs": [
    {"pseudo": "yasin", "score": 3,
     "mots": [{"mot": "JOIE",  "points": 1, "valide": true},
              {"mot": "LIENS", "points": 2, "valide": true}]},
    {"pseudo": "lea", "score": 1,
     "mots": [{"mot": "MIEL",  "points": 1, "valide": true},
              {"mot": "SIENS", "points": 0, "valide": false}]}
  ],
  "gagnant": "yasin"
}');

-- Partie 2 : karim contre yasin, même grille.
INSERT INTO partie (date_debut, date_fin, statut, donnees) VALUES
('2026-11-18 15:00', '2026-11-18 15:03', 'terminee',
'{
  "grille": {
    "lignes": 4,
    "colonnes": 4,
    "lettres": [["E","U","E","S"], ["O","I","N","I"], ["I","J","E","L"], ["M","I","A","E"]]
  },
  "joueurs": [
    {"pseudo": "karim", "score": 3,
     "mots": [{"mot": "JOUE", "points": 1, "valide": true},
              {"mot": "MIE",  "points": 1, "valide": true},
              {"mot": "LIEN", "points": 1, "valide": true}]},
    {"pseudo": "yasin", "score": 1,
     "mots": [{"mot": "LIE",  "points": 1, "valide": true}]}
  ],
  "gagnant": "karim"
}');

INSERT INTO participer (id_utilisateur, id_partie) VALUES
(1, 1), (2, 1),
(3, 2), (1, 2);


-- ---------------------------------------------------------------------
-- 5. Vérifications
-- ---------------------------------------------------------------------

-- Connexion : le mot de passe tapé est haché avec le même sel que celui
-- stocké, puis comparé. Renvoie une ligne si le mot de passe est bon.
SELECT pseudo FROM utilisateur
WHERE pseudo = 'yasin'
  AND mot_de_passe_hash = crypt('yasin123', mot_de_passe_hash);

-- Les deux profils : lea n'apparaît pas dans le public (est_public = FALSE).
SELECT * FROM profil_public;
SELECT * FROM profil_prive WHERE pseudo = 'yasin';


-- ---------------------------------------------------------------------
-- 6. Requêtes avec les opérateurs JSON (début)
-- ---------------------------------------------------------------------

-- -> renvoie du JSON : la grille de chaque partie.
SELECT id_partie, donnees -> 'grille' -> 'lettres' AS grille
FROM partie;

-- ->> renvoie du texte : le gagnant de chaque partie.
SELECT id_partie, donnees ->> 'gagnant' AS gagnant
FROM partie;

-- @> (contient) : les parties où yasin a joué.
SELECT id_partie, statut
FROM partie
WHERE donnees @> '{"joueurs": [{"pseudo": "yasin"}]}';
