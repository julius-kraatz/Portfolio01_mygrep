package de.thws.fiw.kotlin.mygrep

import kotlinx.cli.*
import java.io.File
import kotlin.text.endsWith

data class Settings(
    val ignoreCase: Boolean,
    val printLineNumbers: Boolean,
    val invertSearch: Boolean,
    val pattern: String,
    val directoryName : String,
    val fileName: String?
)

data class LineInfo(
    val content : String,
    val number : Int,
    val fileName : String
)

data class ReadResult(
    val lines: List<LineInfo>,
    val status : Boolean?
)

fun parse(args: Array<String>): Settings
{
    val parser = ArgParser("mygrep")

    val ignoreCase = parser.option(
        type = ArgType.Boolean,
        fullName = "ignorecase",
        shortName = "i",
        description = "Gross- und Kleinschreibung ignorieren"
        ).default(false)

    val printLineNumbers = parser.option(
        type = ArgType.Boolean,
        fullName = "linenumbers",
        shortName = "n",
        description = "Zeilennummern mit angeben"
    ).default(false)

    val invertSearch = parser.option(
        type = ArgType.Boolean,
        fullName = "invert",
        shortName = "v",
        description = "Invertierte Suche"
    ).default(false)

    val pattern = parser.argument(
        type = ArgType.String,
        fullName = "pattern",
        description = "Suchmuster zum Durchsuchen von Text-Dateien"
    )

    val fileOrDirectoryName = parser.argument(
        type = ArgType.String,
        fullName = "fileinfo",
        description = "Datei(.txt) oder Ordner zum Durchsuchen",
    )

    parser.parse(args)

    val directoryName = if (fileOrDirectoryName.value.endsWith(".txt")) "." else fileOrDirectoryName.value
    val fileName = if (fileOrDirectoryName.value.endsWith(".txt")) fileOrDirectoryName.value else null

    return Settings(ignoreCase.value, printLineNumbers.value, invertSearch.value,
        pattern.value, directoryName, fileName)
}

fun readFiles(settings: Settings) : ReadResult
{
    val lines = mutableListOf<LineInfo>()

    val directory = File(settings.directoryName)
    if (!directory.exists() || !directory.isDirectory) return ReadResult(emptyList(), null)

    val textFiles =
        if (settings.fileName == null) directory.listFiles{_, name -> name.endsWith(".txt")}
        else directory.listFiles{_, name -> name.equals(settings.fileName)}

    if(textFiles == null) return ReadResult(emptyList(), null)
    if(textFiles.isEmpty()) return ReadResult(emptyList(), false)

    for(file in textFiles)
    {
        lines.addAll(read(file))
    }
    return ReadResult(lines, true)
}

fun read(file : File) : List<LineInfo>
{
    val lineInfos = mutableListOf<LineInfo>()
    file.readLines().forEachIndexed { index, line ->
        lineInfos.add(LineInfo(line, index + 1, file.name))
    }
    return lineInfos
}

fun search(lines : List<LineInfo>, settings: Settings) : List<String>
{
    val (ignoreCase, printLineNumbers, invertSearch, pattern) = settings

    val output = mutableListOf<String>()
    for(line in lines)
    {
        if(line.content.contains(pattern, ignoreCase) == !invertSearch)
        {
            val filePrefix = if (settings.fileName == null) "(${line.fileName}): " else ""
            val linePrefix = if (printLineNumbers) "|${line.number}| " else ""
            output.add(filePrefix + linePrefix + line.content)
        }
    }
    return output
}

fun main(args: Array<String>)
{
    val settings = parse(args)
    val readResult = readFiles(settings)
    if(readResult.status == null)
    {
        val error =
            "Auf den Ordner ${File(settings.directoryName).absolutePath} konnte nicht zugegriffen werden! " +
                    "Existiert der Ordner?"
        System.err.println(error)
    }
    else if(!readResult.status)
    {
        val error =
            if(settings.fileName == null)
                "Im Ordner ${File(settings.directoryName).absolutePath} konnten keine Text-Dateien gefunden werden!"
            else
                "Die Datei ${File(settings.fileName).name} " +
                        "im aktuellen Ordner ${File(settings.directoryName).absolutePath} " +
                        "konnte nicht gefunden werden!"
        System.err.println(error)
    }
    else
    {
        val output = search(readResult.lines, settings)
        output.forEach(::println)
    }
}