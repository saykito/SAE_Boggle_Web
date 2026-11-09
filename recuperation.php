<?php
require 'config.php';
require 'mail.php';

// Étape 1 : l'utilisateur donne son email, on lui envoie un lien par mail.
// Si le mail ne part pas, on affiche le lien à l'écran.

$message = '';
$lien = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $req = $pdo->prepare('SELECT id FROM utilisateurs WHERE email = ?');
    $req->execute([trim($_POST['email'])]);
    $user = $req->fetch();

    if ($user) {
        $token  = bin2hex(random_bytes(32));                 // jeton aléatoire impossible à deviner
        $expire = date('Y-m-d H:i:s', time() + 3600);        // valable 1h
        $req = $pdo->prepare('UPDATE utilisateurs SET token_recup = ?, token_expire = ? WHERE id = ?');
        $req->execute([$token, $expire, $user['id']]);
        $lien = 'reinitialisation.php?token=' . $token;

        $site = getenv('SITE_URL') ?: 'http://localhost:8000';
        $texte = "Bonjour,\n\nPour changer ton mot de passe, clique sur ce lien (valable 1h) :\n$site/$lien";
        if (envoyerMail(trim($_POST['email']), 'Boggle - mot de passe oublié', $texte)) {
            $lien = ''; // le mail est parti, on n'affiche plus le lien
        }
    }
    // Même message dans tous les cas : on ne révèle pas si l'email existe
    $message = 'Si cet email existe, un lien de réinitialisation a été envoyé.';
}
?>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Boggle - Récupération</title><link rel="stylesheet" href="style.css"></head>
<body>
<div class="boite">
    <h1>Boggle</h1>
    <h2>Mot de passe oublié</h2>
    <?php if ($message): ?>
        <p class="succes"><?= e($message) ?></p>
        <?php if ($lien): ?><p class="info">(Simulation du mail) <a href="<?= e($lien) ?>">Cliquer ici pour changer le mot de passe</a></p><?php endif; ?>
    <?php endif; ?>
    <form method="post">
        <input type="email" name="email" placeholder="Ton email" required>
        <button type="submit">Envoyer le lien</button>
    </form>
    <p><a href="index.php">Retour à la connexion</a></p>
</div>
</body>
</html>
