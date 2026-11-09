<?php
// Connexion à la base + démarrage de la session (inclus en haut de chaque page)

// Cookie de session : illisible en JavaScript et pas envoyé depuis un autre site
session_set_cookie_params(['httponly' => true, 'samesite' => 'Lax']);
session_start();

// En ligne (Render), les infos de connexion sont dans les variables d'environnement.
// En local, on prend les valeurs par défaut.
$dbHote = getenv('DB_HOST') ?: 'localhost';
$dbNom  = getenv('DB_NAME') ?: 'boggle';
$dbUser = getenv('DB_USER') ?: 'postgres';
$dbMdp  = getenv('DB_PASS') ?: 'TON_MOT_DE_PASSE';
$ssl    = getenv('DB_HOST') ? ';sslmode=require' : '';   // Neon impose SSL

$pdo = new PDO("pgsql:host=$dbHote;port=5432;dbname=$dbNom$ssl", $dbUser, $dbMdp);
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

// Raccourci pour afficher du texte sans risque de XSS
function e($texte) {
    return htmlspecialchars($texte, ENT_QUOTES, 'UTF-8');
}

// À appeler en haut des pages réservées aux utilisateurs connectés
function verifierConnexion() {
    if (!isset($_SESSION['id'])) {
        header('Location: index.php');
        exit;
    }
}
