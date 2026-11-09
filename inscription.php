<?php
require 'config.php';

$erreur = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $pseudo = trim($_POST['pseudo']);
    $email  = trim($_POST['email']);
    $mdp    = $_POST['mdp'];

    if (strlen($pseudo) < 3) {
        $erreur = 'Le pseudo doit faire au moins 3 caractères.';
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $erreur = 'Email invalide.';
    } elseif (strlen($mdp) < 8) {
        $erreur = 'Le mot de passe doit faire au moins 8 caractères.';
    } elseif ($mdp !== $_POST['mdp2']) {
        $erreur = 'Les deux mots de passe ne correspondent pas.';
    } else {
        // On vérifie que le pseudo / l'email ne sont pas déjà pris
        $req = $pdo->prepare('SELECT id FROM utilisateurs WHERE pseudo = ? OR email = ?');
        $req->execute([$pseudo, $email]);
        if ($req->fetch()) {
            $erreur = 'Pseudo ou email déjà utilisé.';
        } else {
            $req = $pdo->prepare('INSERT INTO utilisateurs (pseudo, email, mdp) VALUES (?, ?, ?)');
            $req->execute([$pseudo, $email, password_hash($mdp, PASSWORD_DEFAULT)]);
            header('Location: index.php?ok=1');
            exit;
        }
    }
}
?>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Boggle - Inscription</title><link rel="stylesheet" href="style.css"></head>
<body>
<div class="boite">
    <h1>Boggle</h1>
    <h2>Créer un compte</h2>
    <?php if ($erreur): ?><p class="erreur"><?= e($erreur) ?></p><?php endif; ?>
    <form method="post">
        <input type="text" name="pseudo" placeholder="Pseudo" value="<?= e($_POST['pseudo'] ?? '') ?>" required>
        <input type="email" name="email" placeholder="Email" value="<?= e($_POST['email'] ?? '') ?>" required>
        <input type="password" name="mdp" placeholder="Mot de passe (8 caractères min.)" required>
        <input type="password" name="mdp2" placeholder="Confirmer le mot de passe" required>
        <button type="submit">S'inscrire</button>
    </form>
    <p><a href="index.php">J'ai déjà un compte</a></p>
</div>
</body>
</html>
