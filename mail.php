<?php
// Envoi de mail avec PHPMailer (https://github.com/PHPMailer/PHPMailer)
require __DIR__ . '/PHPMailer/Exception.php';
require __DIR__ . '/PHPMailer/PHPMailer.php';
require __DIR__ . '/PHPMailer/SMTP.php';

use PHPMailer\PHPMailer\PHPMailer;

// Retourne true si le mail est bien parti
function envoyerMail($dest, $sujet, $texte) {
    $hote = getenv('SMTP_HOST');
    if (!$hote) {
        return false; // pas de serveur configuré
    }
    $mail = new PHPMailer(true);
    try {
        $mail->isSMTP();
        $mail->Host = $hote;
        $mail->Port = (int) (getenv('SMTP_PORT') ?: 587);
        $mail->Timeout = 10;
        if (getenv('SMTP_USER')) {
            $mail->SMTPAuth = true;
            $mail->Username = getenv('SMTP_USER');
            $mail->Password = getenv('SMTP_PASS');
            $mail->SMTPSecure = 'tls';
        }
        $mail->CharSet = 'UTF-8';
        $mail->setFrom(getenv('SMTP_FROM') ?: 'noreply@boggle.fr', 'Boggle');
        $mail->addAddress($dest);
        $mail->Subject = $sujet;
        $mail->Body = $texte;
        $mail->send();
        return true;
    } catch (Exception $e) {
        return false;
    }
}
