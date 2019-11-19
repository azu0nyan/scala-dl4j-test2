package NotesTest

object Note { //A4
  var refNote = 440
  var refNoteOctave = 4
  var refNoteToneNumber = 9

  def calcFreq(octave: Double, toneNumber: Double): Double = {
    val a = Math.pow(2, 1d / 12d)
    val stepsToRef = getStepsToRef(octave, toneNumber)
    val freq = refNote * Math.pow(a, stepsToRef)
    freq
  }

  private def getStepsToRef(octave: Double, toneNumber: Double) = {
    val refTone = refNoteOctave * 12 + refNoteToneNumber
    val tone = octave * 12 + toneNumber
    tone - refTone
  }

  def getNoteByName(name: String): Note = {
    val octaveNumber = name.replaceAll("[a-zA-Z#]", "").toInt
    val noteName = name.replaceAll("[0-9]", "").toUpperCase
    var toneNumber = 0
    noteName match {
      case "A" =>
        toneNumber = 9
      case "BB" =>
        toneNumber = 10
      case "B" =>
        toneNumber = 11
      case "C" =>
        toneNumber = 0
      case "DB" =>
        toneNumber = 1
      case "D" =>
        toneNumber = 2
      case "EB" =>
        toneNumber = 3
      case "E" =>
        toneNumber = 4
      case "F" =>
        toneNumber = 5
      case "GB" =>
        toneNumber = 6
      case "G" =>
        toneNumber = 7
      case "AB" =>
        toneNumber = 8
    }
    new Note(octaveNumber, toneNumber)
  }
}

case class Note(var octave: Double, var toneNumber: Int ) {
  def freq  :Double= Note.calcFreq(octave, toneNumber)
}
