<?php
require 'config.php';
verifierConnexion();

$req = $pdo->prepare('SELECT p.*, u.pseudo FROM parties p JOIN utilisateurs u ON p.createur_id = u.id WHERE p.id = ?');
$req->execute([(int) ($_GET['id'] ?? 0)]);
$partie = $req->fetch();

if (!$partie) {
    header('Location: parties.php');
    exit;
}

// Grille provisoire 4x4 tirée au hasard.
// Plus tard elle sera fournie par le moteur de jeu en C.
$lettres = 'AAAAAAEEEEEEEEEIIIIIOOOOUUUUBCCDDDFGGHJLLLLMMMNNNNNNPPQRRRRRRSSSSSSTTTTTTVXYZ';
$grille = [];
for ($i = 0; $i < 16; $i++) {
    $grille[] = $lettres[random_int(0, strlen($lettres) - 1)];
}
?>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Boggle - <?= e($partie['nom']) ?></title><link rel="stylesheet" href="style.css"></head>
<body>
<header>
    <span><a href="parties.php">← Parties</a></span>
    <span>Connecté : <?= e($_SESSION['pseudo']) ?> · <a href="deconnexion.php">Déconnexion</a></span>
</header>
<div class="page">
    <h2><?= e($partie['nom']) ?></h2>
    <p><small>Créée par <?= e($partie['pseudo']) ?> le <?= e($partie['date_creation']) ?></small></p>

    <div class="plateau">
        <?php foreach ($grille as $l): ?><div class="case"><?= $l ?></div><?php endforeach; ?>
    </div>
</div>
</body>
</html>
