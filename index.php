<?php
require 'config.php';

if (isset($_SESSION['id'])) {      // déjà connecté -> direct aux parties
    header('Location: parties.php');
    exit;
}

$erreur = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $req = $pdo->prepare('SELECT * FROM utilisateurs WHERE pseudo = ?');
    $req->execute([$_POST['pseudo']]);
    $user = $req->fetch();

    if ($user && password_verify($_POST['mdp'], $user['mdp'])) {
        session_regenerate_id(true);
        $_SESSION['id'] = $user['id'];
        $_SESSION['pseudo'] = $user['pseudo'];
        header('Location: parties.php');
        exit;
    }
    $erreur = 'Pseudo ou mot de passe incorrect.';
}
?>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Boggle - Connexion</title><link rel="stylesheet" href="style.css"></head>
<body>
<div class="boite">
    <h1>Boggle</h1>
    <p>Trouve un maximum de mots dans une grille de 16 lettres, contre d'autres joueurs.</p>
    <div class="plateau mini">
        <?php foreach (str_split('MOTSJEUXGRILLEAB') as $l): ?><div class="case"><?= $l ?></div><?php endforeach; ?>
    </div>
    <!-- 1. Création de compte -->
    <h2>Nouveau joueur ?</h2>
    <a class="bouton" href="inscription.php">Créer un compte</a>

    <!-- 2. Connexion -->
    <h2>Connexion</h2>
    <?php if ($erreur): ?><p class="erreur"><?= e($erreur) ?></p><?php endif; ?>
    <?php if (isset($_GET['ok'])): ?><p class="succes">C'est bon, tu peux te connecter.</p><?php endif; ?>
    <form method="post">
        <input type="text" name="pseudo" placeholder="Pseudo" required>
        <input type="password" name="mdp" placeholder="Mot de passe" required>
        <button type="submit">Se connecter</button>
    </form>

    <!-- 3. Récupération du mot de passe -->
    <p><a href="recuperation.php">Mot de passe oublié ?</a></p>
</div>
</body>
</html>
