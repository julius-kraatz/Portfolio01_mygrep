# Portfolio01_mygrep

## Beschreibung
mygrep ist eine Kommandozeilenanwendung zur Suche nach Textmustern in Dateien, die dem Programm  *GNU grep* ähnelt. Dabei können einzelne Dateien oder Ordner durchsucht werden. Das Programm gibt sämtliche Zeilen aus, in denen das angegebene Textmuster vorkommt. Optional ist es möglich, Groß- und Kleinschreibung zu ignorieren, sich die Zeilennummern ausgeben zu lassen oder eine invertierte Suche durchzuführen.

## Beispielaufruf
```
./gradlew run --args="-f testfiles/input.txt World"
```
Gibt alle Zeilen der Datei *testfiles/input.txt* aus, in denen das Wort *World* vorkommt.

## Kompilieren und Ausführen
Befindet man sich im Hauptordner dieses Projekts ("Portfolio01_mygrep"), so gilt folgendes:
### Kompilieren
Das Programm wird mit folgendem Kommandozeilenbefehl kompiliert:
```
./gradlew build
```
Alternativ:
```
./gradlew clean build
```
### Ausführen
Das Programm wird allgemein mit folgendem Kommandozeilenbefehl ausgeführt:
```
./gradlew run --args="<argumente>"
```
Alternativ:
```
java -jar build/libs/mygrep.jar <argumente>
```
Der genaue Inhalt der *\<argumente\>* wird im weiteren Verlauf beschrieben.

## Argumente
```
[-f Datei | -d Verzeichnis] [-i] [-n] [-v] Suchmuster
 ```
Die Angabe eines Suchmusters ist immer Pflicht. Die Angabe von Datei **oder** Verzeichnis ist Pflicht. Die weiteren Optionen können beliebig kombiniert werden und haben folgende Auswirkungen:
- -i: Groß- und Kleinschreibung ignorieren
- -n: Zeilennummern mit angeben (Bei Suche in Verzeichnis auch Dateinamen mit angeben)
- -v: Invertierte Suche: Es werden die Zeilen ausgegeben, in denen das Suchmuster **nicht** vorkommt.

## Weitere Beispielaufrufe
### Verzeichnis durchsuchen
```
./gradlew run --args="-d testfiles World"
```
Gibt alle Zeilen sämtlicher Textdateien im Ordner *testfiles* aus (keine Unterordner), in denen das Wort *World* vorkommt.
### Verzeichnis durchsuchen, Groß- und Kleinschreibung ignorieren, Zeilennummern ausgeben
```
./gradlew run --args="-d testfiles -i -n World" 
```
Gibt alle Zeilen sämtlicher Textdateien im Ordner *testfiles* aus (keine Unterordner), in denen das Wort *World* vorkommt und beachtet dabei nicht die Groß- und Kleinschreibung der einzelnen Buchstaben. Die Zeilennummern und die zugehörigen Dateinamen werden ebenso angezeigt.
### Datei durchsuchen, Groß- und Kleinschreibung ignorieren, Suche invertieren
```
./gradlew run --args="-f testfiles/input.txt -i -v World"
```
Gibt alle Zeilen der Datei *testfiles/input.txt* aus, in denen das Wort *World* **in keiner erdenklichen Schreibweise** (in Bezug auf Groß- und Kleinschreibung) vorkommt. Grund dafür ist: Das Ignorieren der Groß- und Kleinschreibung (*-i*) geschieht im Programm vor dem Invertieren der Suche (*-v*), da letzteres immer zum Schluss erfolgt.

## Allgemeines
- Wie man dem Dokument entnehmen kann, wurden alle drei Stufen umgesetzt.
  1. Basisfunktionalität: Ausführen des Programms mit Suchmuster
  2. Optionen: -i, -n, -v
  3. Suche in Verzeichnis: -d, sonst -f zur Suche in nur einer Datei
- Die zum Testen verwendeten Dateien befinden sich im Unterordner *testfiles*.
- Bei Angabe eines Ordners wird explizit nur nach .txt-Dateien gesucht. Bei Angabe einer einzelnen Datei wird die Dateiendung nicht überprüft. So kann man mit dem Programm z.B. auch Markdown-Dateien durchsuchen.
- Bei Angabe der Option *-h* innerhalb der *\<argumente\>* wird ein Hilfetext ausgegeben, anstatt das Programm auszuführen. Der Hilfetext erscheint auch bei falscher Angabe von Argumenten.
- Gibt das Programm überhaupt nichts aus, dann wurde das Suchmuster in der angegebenen Datei bzw. Ordner nicht gefunden. Existiert die Datei bzw. der Ordner nicht, dann gibt es eine explizite Fehlermeldung.

