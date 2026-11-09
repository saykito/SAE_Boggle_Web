<?php
require 'config.php';
verifierConnexion();

// Création d'une nouvelle partie
if ($_SERVER['REQUEST_METHOD'] === 'POST' && trim($_POST['nom']) !== '') {
    $req = $pdo->prepare('INSERT INTO parties (nom, createur_id, date_creation) VALUES (?, ?, ?)');
    $req->execute([trim($_POST['nom']), $_SESSION['id'], date('Y-m-d H:i:s')]);
    header('Location: parties.php');
    exit;
}

// Liste des parties avec le pseudo du créateur (jointure)
$parties = $pdo->query(
    'SELECT p.id, p.nom, p.date_creation, u.pseudo
     FROM parties p JOIN utilisateurs u ON p.createur_id = u.id
     ORDER BY p.date_creation DESC'
)->fetchAll();
?>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Boggle - Parties</title><link rel="stylesheet" href="style.css"></head>
<body>
<header>
    <span>Boggle</span>
    <span>Connecté : <?= e($_SESSION['pseudo']) ?> · <a href="deconnexion.php">Déconnexion</a></span>
</header>
<div class="page">
    <h2>Parties</h2>

    <form method="post" class="ligne">
        <input type="text" name="nom" placeholder="Nom de la nouvelle partie" required>
        <button type="submit">Nouvelle partie</button>
    </form>

    <?php if (!$parties): ?>
        <p>Aucune partie pour l'instant.</p>
    <?php else: ?>
        <div class="grille-parties">
        <?php foreach ($parties as $p): ?>
            <a class="carte" href="partie.php?id=<?= $p['id'] ?>">
                <strong><?= e($p['nom']) ?></strong>
                <small>par <?= e($p['pseudo']) ?></small>
            </a>
        <?php endforeach; ?>
        </div>
    <?php endif; ?>
</div>
</body>
</html>
