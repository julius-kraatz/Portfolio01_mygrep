# Reflexion (Portfolio01_mygrep) 

## Entwurfsentscheidungen
Da das Programm von geringem Umfang ist und wir uns mit dem Thema “objektorientiertes Programmieren” in Kotlin noch nicht beschäftigt hatten, wurde das Programm wie folgt aufgebaut:
- Der gesamte Inhalt kommt in eine Datei. Es gibt keine Klassen (nur enum und data class).
- Die Funktion main ruft die Funktionen für Argumentverarbeitung, Dateizugriff und Suchlogik auf. Die Status-Rückgabe der einzelnen Funktionen wird ggf. in eine Fehler-Nachricht umgewandelt und das Programm beendet. Am Ende gibt main die gefundenen Zeilen aus.
- Dabei gibt es keine globalen Variablen, sondern alle nötigen Informationen werden von Funktionen zurückgegeben und ggf. an die nächste Funktion übergeben.
- Die Suche in einer einzelnen Datei wird als Sonderfall der Suche in einem Ordner betrachtet, damit die Logik des Dateizugriffs wiederverwendet werden kann. Dennoch werden Datei-Fehler und Ordner-Fehler in den Fehler-Nachrichten unterschieden.
- Ich habe frühe Return-Statements verwendet, da diese von der IDE bisweilen empfohlen werden, um Verschachtelung zu reduzieren (bei Klick auf das Glühbirnensymbol).
- Ich habe dem Projekt keine Unit-Tests hinzugefügt, da das nicht gefordert war und habe es stattdessen wie empfohlen mit kleinen Beispieldateien getestet. 

## Schwierigkeiten
Bei der Umsetzung von Stufe 3 ergab sich das folgende Problem:
- Durch Auswertung eines Strings kann nicht eindeutig entschieden werden, ob der User damit eine Datei oder einen Ordner angeben wollte.
- Deshalb musste eine explizite Option -f zur Angabe des Dateinamens (fileName) bzw. -d zur Angabe des Ordnernamens (directoryName) hinzugefügt werden. Als Konsequenz musste auch der Fehlerfall überprüft werden, dass der User beide oder keine der Optionen angibt.
- kotlinx.cli ermöglicht es nicht, als Default-Wert für Optionen null anzugeben. Deshalb wurde für die Variablen fileName und directoryName ein leerer String als Default-Wert angegeben. Die enum-Variable target wurde nachher genutzt, um zu entscheiden, ob auf fileName oder directoryName zugegriffen werden soll. Da target bereits zur Darstellung des oben beschriebenen Fehlerfalls eingeführt worden war, war dies die einfachste Möglichkeit.

## KI-Werkzeuge
Ich habe folgendes von GPT-4o mini übernommen und musste keine Anpassungen vornehmen:
- Enum in Kotlin, Tuple in Kotlin (letzteres führte zur data class)
- Lesen aller Textdateien in einem Ordner (File.listFiles mit filter)
- einfachster Weg zum Lesen einer Datei (File.readLines mit forEachIndexed und action)
- Drucken mit .forEach (println als action)
- mögliche Exceptions bei Zugriff auf Ordner oder Datei (IOException beim Lesen)
- Erstellen einer fat JAR ohne Plugin (tasks.jar in build.gradle.kts)

Beim Umgang mit kotlinx.cli (siehe Schwierigkeiten) konnte mir GPT-4o mini nicht helfen. Eine leistungsfähigere KI hätte das Problem möglicherweise gelöst.