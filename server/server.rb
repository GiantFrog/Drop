require 'socket'
require './leaderboard_entry.rb'

server = TCPServer.new 9027
file = File.open('leaderboard.txt', 'r')
leaderboard = Array.new

unless file.eof?	#load the leaderboard
	file.each_line do |line|
		parts = line.chomp.split ': '
		leaderboard << (LeaderboardEntry.new parts[0], parts[1])
	end
	puts 'leaderboard loaded!'
end
file.close()

loop do		#accept new entries
  client = server.accept	#wait here until somebody connects
  new_entry_parts = client.gets.chomp.split ':'
	puts new_entry_parts[0] + ' has connected!'
	new_entry = LeaderboardEntry.new new_entry_parts[0], new_entry_parts[1]

	added = false
	leaderboard.each do |entry|	#add them to the leaderboard
		if new_entry.score > entry.score
			leaderboard.insert entry.index new_entry
			added = true
			break
		end
	end
	unless added
		leaderboard << new_entry
	end

	file = File.open('leaderboard.txt', 'w+')
	leaderboard.each do |entry|	#save the file
		file.puts(entry.name + ': ' + entry.score)
	end

	client.puts File.read 'leaderboard.txt'
	puts 'saved and sent file!'
	client.close
	file.close
end