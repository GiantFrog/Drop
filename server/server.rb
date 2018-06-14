require 'socket'
require './leaderboard_entry.rb'

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

loop do		#accept new entries
  client = server.accept	#wait here until somebody connects
  new_entry_parts = client.gets.chomp.split ':'
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

	#save the file
	file = File.open('leaderboard.txt', 'w+')
	leaderboard.each do |entry|
		file.puts(entry.name + ': ' + entry.score.to_s)
	end
	file.puts '&'
	file.close

	#give the client the file
	board_string = IO.read('leaderboard.txt')
	board_string.each_line do |line|
		client.puts line
	end

	client.gets		#wait for client to do their thing
	puts 'saved and sent file!'
	client.close
end