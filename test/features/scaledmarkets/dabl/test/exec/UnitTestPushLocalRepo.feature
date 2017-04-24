# language: en

@unit @exec
Feature: UnitTestLocalRepo
	
	@done
	Scenario: A single include pattern to push
		Given there are two files in a directory, a.txt and b.txt
		When the include pattern specifies b.txt
		Then only file b.txt is pushed
	
	@done
	Scenario: A single exclude pattern to push
		Given there are two files in a directory, a.txt and b.txt
		When the exclude pattern specifies b.txt
		Then no files are pushed
	
	@done
	Scenario: One include pattern, and exclude one file, to push
		Given there are two files in a directory, a.txt and b.txt
		When the include pattern specifies a.txt and b.txt, and the exclude pattern specifies b.txt
		Then only file a.txt is pushed
	
	@done
	Scenario: One include pattern, and exclude files that match a specified extension to push
		Given a directory contains files a.txt, b.txt, a.html, b.html, a.rtf, b.rtf
		When the include pattern specifies a.* and the exclude pattern specifies *.txt
		Then only the files a.html and a.rtf are pushed
	
	@done
	Scenario: Include a directory hierarchy
		Given a directory with files a.txt, b.txt, d/a.txt, d/b.txt, d/dd/a.txt, d/dd/b.txt
		When the include pattern specifies **/a.txt
		Then the files a.txt, d/a.txt, and d/dd/a.txt are pushed
	
	@done
	Scenario: One include pattern, and exclude a directory to push
		Given
		When 
		Then
	
	@done
	Scenario: Two include patterns, and exclude files that match a specified extension to push
		Given there are files in each two directories that have a .txt extension
		When 
		Then
