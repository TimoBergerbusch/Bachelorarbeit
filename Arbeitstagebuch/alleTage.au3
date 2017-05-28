#include <File.au3>
#include <FileConstants.au3>
#include <MsgBoxConstants.au3>
#include <WinAPIFiles.au3>


if not FileExists(@scriptDir & "/Tage/" & @MDAY & "." & @MON & "." & @YEAR & ".tex") Then
   FileCopy(@scriptdir & "/Tage/defaulttag.tex", @scriptDir & "/Tage/" & @MDAY & "." & @MON & "." & @YEAR &".tex")
   $file = FileOpen(@scriptDir & "/Tage/" & @MDAY & "." & @MON & "." & @YEAR & ".tex")
   FileWriteLine($file,"\section*{" & @MDAY & "." & @MON & "." & @YEAR &"}")
   FileClose($file)
EndIf

$files = _FileListToArray(@ScriptDir & "/Tage/")
$file = FileOpen(@scriptdir & "/alleTage.tex", 2+256)

for $i=1 to $files[0] step 1
   If $files[$i] <> "defaulttag.tex" Then
	  FileWriteLine($file, "\input{ Tage/" & $files[$i] & " } ")
   EndIf
Next

FileClose($file)