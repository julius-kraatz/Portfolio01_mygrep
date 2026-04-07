package de.thws.fiw.kotlin.mygrep

import kotlinx.cli.*
import kotlin.system.exitProcess
import java.io.File

fun main(args: Array<String>)
{
    val parser = ArgParser("mygrep")

    val pattern = parser.argument(
        type = ArgType.String,
        fullName = "pattern",
        description = "Suchmuster zum Durchsuchen von Text-Dateien"
    )
    val filename = parser.argument(
        type = ArgType.String,
        fullName = "file",
        description = "Text-Datei zum Durchsuchen"
    )

    parser.parse(args)

    val file = File(filename.value)
    if(!file.exists())
    {
        System.err.println("Die Datei ${file.name} konnte nicht gefunden werden!")
        exitProcess(1)
    }

    //val result = mutableListOf<String>()
    val lines = file.readLines()
    for(line in lines)
    {
        if(line.contains(pattern.value))
        {
            println(line)
        }
    }


    //print("Pattern: ${pattern.value}, File: ${file.value}")

}