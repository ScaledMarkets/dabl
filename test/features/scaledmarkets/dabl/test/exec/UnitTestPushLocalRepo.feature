# language: en

@unit @exec @pushlocalrepo
Feature: UnitTestPushLocalRepo
	
	@done
	Scenario: 1: A single include pattern to push
		Given there are two files in directory 1, a.txt and b.txt
		When the include pattern specifies b.txt
		Then only file b.txt is pushed
	
	@done
	Scenario: 2; A single exclude pattern to push
		Given there are two files in directory 2, a.txt and b.txt
		When the exclude pattern specifies b.txt
		Then no files are pushed
	
	@done
	Scenario: 3: One include pattern, and exclude one file, to push
		Given there are two files in directory 3, a.txt and b.txt
		When the include pattern specifies a.txt and b.txt, and the exclude pattern specifies b.txt
		Then only file a.txt is pushed
	
	@done
	Scenario: 4: One include pattern, and exclude files that match a specified extension to push
		Given directory 4 contains files a.txt, b.txt, a.html, b.html, a.rtf, b.rtf
		When the include pattern specifies a.* and the exclude pattern specifies *.txt
		Then only the files a.html and a.rtf are pushed
	
	@done
	Scenario: 5: Include a directory hierarchy
		Given directory 5 with files a.txt, b.txt, d/a.txt, d/b.txt, d/dd/a.txt, d/dd/b.txt
		When the include pattern specifies **
		Then the files a.txt, d/a.txt, and d/dd/a.txt are pushed
	
	@done
	Scenario: 6: One include pattern, and exclude a directory to push
		Given directory 6 with files a.txt, a.rtf, d/a.txt, d/a.rtf, e/a.txt, e/a.rtf, d/dd/a.txt, d/dd/a.rtf
		When the include pattern specifies ** and the exclude pattern specifies e
		Then the files a.txt, d/a.txt, and d/dd/a.txt are pushed
	
	@done
	Scenario: 7: Two include patterns, and exclude files that match a specified extension to push
		Given directory 7 with files a.txt, a.rtf, d/a.txt, d/a.rtf, e/a.txt, e/a.rtf, d/dd/a.txt, d/dd/a.rtf
		When the include pattern specifies ** and the exclude pattern specifies *.txt
		Then the files a.rtf, d/a.rtf, d/dd/a.rtf, e/a.rtf are pushed
