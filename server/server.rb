require 'socket'
require './leaderboard_entry.rb'

def process_connection client
	begin
		#start by sending them the leaderboard
		board_string = IO.read('leaderboard.txt')
		board_string.each_line do |line|
			client.puts line
		end

		#get the new score
		response = client.gets.chomp
		unless response == 'nope'
			new_entry_parts = response.split ':'

			#mm/dd/yyyy hr:mn- Umiko has connected!
			puts new_entry_parts[0] + ' has connected!'
			new_entry = LeaderboardEntry.new new_entry_parts[0], new_entry_parts[1].to_i

			added = false		#add them to the leaderboard
			leaderboard.each_with_index do |entry, index|
				if new_entry.score > entry.score
					leaderboard.insert index, new_entry
					added = true
					break
				end
			end
			unless added		#put it at the end
				leaderboard << new_entry
			end
			if leaderboard.size > 44	#axe the lowest score if it won't fit on the client's screen
				leaderboard.pop
			end


			#save the file while sending each line off to the client
			file = File.open('leaderboard.txt', 'w+')
			leaderboard.each do |entry|
				file.puts(entry.name + ': ' + entry.score.to_s)
				client.puts(entry.name + ': ' + entry.score.to_s)
			end
			file.print '&'	#tell the client we have reached the end of the leaderboard
			file.close
		end
		client.close
	rescue
		#try to close the connection if things go south, then start anew
		puts 'something broke, rescuing...'
		begin
			client.close
		rescue
			#connection isn't open. okay.
		end
	end
end

#program starts here!
server = TCPServer.new 9027
file = File.open('leaderboard.txt', 'r')
leaderboard = Array.new

unless file.eof?	#load the leaderboard
	file.each_line do |line|
		unless line.chomp == '&'
			parts = line.chomp.split ': '
			leaderboard << (LeaderboardEntry.new parts[0], parts[1].to_i)
		end
	end
	puts 'leaderboard loaded!'
end
file.close()

loop do		#accept new entries as soon as they come
	Thread.new {process_connection(server.accept)}
end