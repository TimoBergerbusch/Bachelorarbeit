#include <File.au3>
#include <FileConstants.au3>
#include <MsgBoxConstants.au3>
#include <WinAPIFiles.au3>


$name = @scriptDir & "/Tage/" & @MDAY & "." & @MON & "." & @YEAR
if not FileExists($name & ".tex") Then
   FileCopy(@scriptdir & "/Tage/defaulttag.tex", $name  &".tex")
   $file = FileOpen($name & ".tex")
   FileWriteLine($file,"\section*{" & StringSplit($name,"/")[3] &"}")
   FileClose($file)
EndIf
ShellExecute("Arbeitstagebuch.tex")
ShellExecute($name&".tex")


$files = _FileListToArray(@ScriptDir & "/Tage/")
$file = FileOpen(@scriptdir & "/alleTage.tex", 2+256)

$files = sortEntrys($files)

;_ArrayDisplay($files)

for $i=1 to $files[0] step 1
   If $files[$i] <> "defaulttag.tex" Then
	  FileWriteLine($file, "\input{Tage/" & $files[$i] & "} ")
   EndIf
Next

FileClose($file)

Func sortEntrys($files)
   for $i=1 to $files[0] step 1
	  for $j=$i+1 to $files[0] step 1
		 If $files[$i] <> "defaulttag.tex" And $files[$j] <> "defaulttag.tex" Then
			$date1 = StringSplit($files[$i],".")
			$date2 = StringSplit($files[$j],".")
			;MsgBox(0,"",$date1[1] & "." & $date1[2] & @CRLF & $date2[1] & "." & $date2[2])
			if $date2[2] < $date1[2] Then
			   $files=swap($files,$i,$j)
			   ;MsgBox(0,"","swap: " & $date1[1] & "." & $date1[2] & @CRLF & $date2[1] & "." & $date2[2])
			ElseIf $date2[2] = $date1[2] And $date2[1] < $date1[1] Then
			   $files=swap($files,$i,$j)
			   ;MsgBox(0,"","swap: " & $date1[1] & "." & $date1[2] & @CRLF & $date2[1] & "." & $date2[2])
			EndIf
		 EndIf
	  Next
   Next
   Return $files
EndFunc

Func swap($files, $i, $j)
   $tmp = $files[$i]
   $files[$i]=$files[$j]
   $files[$j]=$tmp
   Return $files
EndFunc