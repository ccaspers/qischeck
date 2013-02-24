QISCheck
========

Einfache Android-App zum Anzeigen des Notenspiegels aus dem QIS-System
der Hochschule RheinMain. Da keine API vorhanden ist wird die Internetseite mit JSoup
geparsed.

##Features
* Übserichtliche Darstellung
* automatische Synchronisierung
* Sync-Intervall einstellbar
* Benachrichtigungen bei neuen Noten

##Download
* Dropbox: https://dl.dropbox.com/u/6910770/QISCheck.apk

##Hinweis
Die Zugangsdaten zum QIS-System werden in den SharedPreferences im Klartext gespeichert.
Auf Geräten mit Root-Zugriff stellt das ein Sicherheitsrisiko dar, da andere Anwendungen
dadurch Zugriff auf die Privaten Daten der App erhalten könnten. 