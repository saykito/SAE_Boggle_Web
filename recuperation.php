<?php
require 'config.php';

// Étape 1 : l'utilisateur donne son email, on génère un lien de réinitialisation.
// En vrai on enverrait ce lien par mail.
// Ici, pas de serveur mail en local, donc on l'affiche directement à l'écran.

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
