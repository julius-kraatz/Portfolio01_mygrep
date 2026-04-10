package de.thws.fiw.kotlin.mygrep

import kotlinx.cli.*
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess
import kotlin.text.endsWith

enum class Target
{
    NONE,
    FILE,
    DIRECTORY,
    BOTH
}

data class Settings(
    val ignoreCase: Boolean,
    val printLineNumbers: Boolean,
    val invertSearch: Boolean,
    val pattern: String,
    val fileName: String,
    val directoryName: String,
    val target : Target
)

enum class FileStatus
{
    SUCCESS,
    NOT_FOUND,
    NOT_READ,
}

enum class DirectoryStatus
{
    SUCCESS,
    NOT_FOUND,
    NOT_READ,
}

data class FileResult(
    val file : File,
    val lines: List<LineInfo>,
    val status : FileStatus
)

data class DirectoryResult(
    val fileResults : List<FileResult>,
    val errorFileResult : FileResult?,
    val status : DirectoryStatus
)

data class LineInfo(
    val content : String,
    val number : Int,
    val fileName : String
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

    val file = parser.option(
        type = ArgType.String,
        fullName = "file",
        shortName = "f",
        description = "Text-Datei zum Durchsuchen"
    ).default("")

    val directory = parser.option(
        type = ArgType.String,
        fullName = "directory",
        shortName = "d",
        description = "Ziel-Ordner (nur .txt-Dateien werden durchsucht)"
    ).default("")

    val pattern = parser.argument(
        type = ArgType.String,
        fullName = "pattern",
        description = "Suchmuster zum Durchsuchen von Text-Dateien"
    )

    parser.parse(args)

    val enteredFile = file.value.isNotEmpty()
    val enteredDirectory = directory.value.isNotEmpty()
    val target = when {
        enteredFile && enteredDirectory -> Target.BOTH
        enteredFile && !enteredDirectory -> Target.FILE
        !enteredFile && enteredDirectory -> Target.DIRECTORY
        else -> Target.NONE
    }

    return Settings(ignoreCase.value, printLineNumbers.value, invertSearch.value, pattern.value,
        file.value, directory.value, target)
}

fun readFiles(settings: Settings) : DirectoryResult
{
    if(settings.target == Target.FILE)
    {
        val file = File(settings.fileName)
        val fileResult = readFile(file)
        val errorFileResult = if (fileResult.status == FileStatus.SUCCESS) null else fileResult
        return DirectoryResult( listOf(fileResult), errorFileResult, DirectoryStatus.SUCCESS)
    }

    val directory = File(settings.directoryName)
    if (!directory.exists() || !directory.isDirectory)
        return DirectoryResult(emptyList(), null, DirectoryStatus.NOT_FOUND)

    val textFiles = directory.listFiles{_, name -> name.endsWith(".txt")}
    if(textFiles == null)
        return DirectoryResult(emptyList(), null, DirectoryStatus.NOT_READ)

    val results = mutableListOf<FileResult>()
    for(file in textFiles)
    {
        val result = readFile(file)
        results.add(result)
        if(result.status != FileStatus.SUCCESS)
        {
            return DirectoryResult(results, result, DirectoryStatus.SUCCESS)
        }
    }
    return DirectoryResult(results, null, DirectoryStatus.SUCCESS)
}

fun readFile(file : File) : FileResult
{
    val lineInfos = mutableListOf<LineInfo>()

    if(!file.exists()) return FileResult(file, lineInfos, FileStatus.NOT_FOUND)

    try
    {
        file.readLines().forEachIndexed { index, line ->
            lineInfos.add(LineInfo(line, index + 1, file.name))
        }
    }
    catch (_: IOException)
    {
        return FileResult(file, lineInfos, FileStatus.NOT_READ)
    }

    return FileResult(file, lineInfos, FileStatus.SUCCESS)
}

fun search(results : List<FileResult>, settings: Settings) : List<String>
{
    val (ignoreCase, printLineNumbers, invertSearch, pattern, fileName, directoryName, target) = settings

    val output = mutableListOf<String>()
    for(result in results)
    {
        for(line in result.lines)
        {
            if (line.content.contains(pattern, ignoreCase) == !invertSearch)
            {
                val filePrefix = if (target == Target.DIRECTORY) "(${line.fileName}): " else ""
                val linePrefix = if (printLineNumbers) "|${line.number}| " else ""
                output.add(filePrefix + linePrefix + line.content)
            }
        }
    }
    return output
}

fun abort(message : String)
{
    System.err.println(message)
    exitProcess(1)
}

fun main(args: Array<String>) {
    val settings = parse(args)
    when (settings.target) {
        Target.NONE ->
            abort("Es wurde weder die Option -f <Dateiname.txt> noch die Option -d <Ordner> angegeben!")
        Target.BOTH ->
            abort("Es wurden beide Optionen -f <Dateiname.txt> und -d <Ordner> angegeben!" +
                    " Bitte nur eine der Optionen angeben!")
        else -> {}
    }

    val result = readFiles(settings)
    when (result.status) {
        DirectoryStatus.NOT_FOUND ->
            abort("Der Ordner ${File(settings.directoryName).absolutePath} " +
                    "konnte nicht gefunden werden!")
        DirectoryStatus.NOT_READ ->
            abort("Auf den Ordner ${File(settings.directoryName).absolutePath} " +
                    "konnte nicht zugegriffen werden!")
        DirectoryStatus.SUCCESS -> {}
    }

    val errorFile = result.errorFileResult
    if (errorFile != null) {
        when (errorFile.status) {
            FileStatus.NOT_FOUND -> abort("Die Datei ${errorFile.file.absolutePath} konnte nicht gefunden werden!")
            FileStatus.NOT_READ -> abort("Auf die Datei ${errorFile.file.absolutePath} konnte nicht zugegriffen werden!")
            FileStatus.SUCCESS -> {}
        }
    }

    val output = search(result.fileResults, settings)
    output.forEach(::println)
}