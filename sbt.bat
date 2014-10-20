SET LAUNCHER_LOCAL=%HOMEDRIVE%%HOMEPATH%\.sbt\sbt-launch.jar


IF NOT EXIST %LAUNCHER_LOCAL% (
    REM echo //SBT launcher is not present on '%LAUNCHER_LOCAL%', try to download: 
	mkdir %HOMEDRIVE%%HOMEPATH%\.sbt
    
    REM create Java source file for download SBT
    echo import java.io.*; > LauncherDownloader.java
    echo import java.net.*; >> LauncherDownloader.java
    echo class LauncherDownloader { >> LauncherDownloader.java
    echo public static void main(String[] args^) >> LauncherDownloader.java

    echo throws MalformedURLException, IOException { saveUrl( >> LauncherDownloader.java
    echo "%LAUNCHER_LOCAL:\=/%", "http://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/0.13.0/sbt-launch.jar"^); >> LauncherDownloader.java
    echo }  >> LauncherDownloader.java

    REM //http://stackoverflow.com/questions/921262/how-to-download-and-save-a-file-from-internet-using-java#answer-921408
    echo public static void saveUrl(String filename, String urlString^) throws MalformedURLException, IOException { >> LauncherDownloader.java
    echo BufferedInputStream in = null; FileOutputStream fout = null; try { >> LauncherDownloader.java
    echo in = new BufferedInputStream(new URL(urlString^).openStream(^)^); fout = new FileOutputStream(filename^); >> LauncherDownloader.java
    echo byte data[] = new byte[1024]; int count; while ((count = in.read(data, 0, 1024^)^) != -1^) fout.write(data, 0, count^); >> LauncherDownloader.java
    echo } finally { if (in != null^) in.close(^); if (fout != null^) fout.close(^); } } }  >> LauncherDownloader.java


    javac LauncherDownloader.java && java LauncherDownloader
    del LauncherDownloader.java LauncherDownloader.class
)

java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=768M -jar %LAUNCHER_LOCAL% %*
