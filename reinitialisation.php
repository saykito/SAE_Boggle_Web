<?php
require 'config.php';

// Étape 2 : l'utilisateur arrive avec le jeton et choisit un nouveau mot de passe
$token = $_GET['token'] ?? '';
$req = $pdo->prepare('SELECT id FROM utilisateurs WHERE token_recup = ? AND token_expire > ?');
$req->execute([$token, date('Y-m-d H:i:s')]);
$user = $req->fetch();

$erreur = '';
if (!$user) {
    $erreur = 'Lien invalide ou expiré.';
} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (strlen($_POST['mdp']) < 8) {
        $erreur = 'Le mot de passe doit faire au moins 8 caractères.';
    } elseif ($_POST['mdp'] !== $_POST['mdp2']) {
        $erreur = 'Les deux mots de passe ne correspondent pas.';
    } else {
        // Nouveau hash + on supprime le jeton pour qu'il ne serve qu'une fois
        $req = $pdo->prepare('UPDATE utilisateurs SET mdp = ?, token_recup = NULL, token_expire = NULL WHERE id = ?');
        $req->execute([password_hash($_POST['mdp'], PASSWORD_DEFAULT), $user['id']]);
        header('Location: index.php?ok=1');
        exit;
    }
}
?>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Boggle - Nouveau mot de passe</title><link rel="stylesheet" href="style.css"></head>
<body>
<div class="boite">
    <h1>Boggle</h1>
    <h2>Nouveau mot de passe</h2>
    <?php if ($erreur): ?><p class="erreur"><?= e($erreur) ?></p><?php endif; ?>
    <?php if ($user): ?>
    <form method="post">
        <input type="password" name="mdp" placeholder="Nouveau mot de passe" required>
        <input type="password" name="mdp2" placeholder="Confirmer" required>
        <button type="submit">Valider</button>
    </form>
    <?php endif; ?>
    <p><a href="index.php">Retour à la connexion</a></p>
</div>
</body>
</html>
