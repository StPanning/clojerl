%%%-------------------------------------------------------------------
%%% @author Steffen Panning <>
%%% @copyright (C) 2014, Steffen Panning
%%% @doc
%%%
%%% @end
%%% Created : 28 Jan 2014 by Steffen Panning <>
%%%-------------------------------------------------------------------
-module(erl_node).

-export([start/0, init/1]).


start() ->
  io:format("start..~n",[]),
  Pid = spawn(erl_node, init, [self()]),
  true = register(erl_node, Pid),
  io:format("registered: ~p ~n",[registered()]).

init(From) ->
  loop(From).

loop(From) ->
  receive
    {Pid, {sum, A, B}}  -> Pid ! {sum, A + B},
                           io:format("RPC: ~p~n",[{sum, A, B}]),
                           loop(From);
    {Pid, {echo, What}} -> Pid ! {echo, What},
                           io:format("RPC: ~p~n",[{echo, What}]),
                           loop(From);
    Any                 -> io:format("Unkown: ~p~n",[Any]),
                           loop(From)

  end.
