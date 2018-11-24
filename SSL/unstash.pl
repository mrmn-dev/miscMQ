#!/usr/bin/perl

# usage perl unstash.pl <Stash file name ex stash.sth>
# this will print the password from within the stash file

use strict;

die "Usage: $0 <stash file>\n" if $#ARGV != 0;

my $file=$ARGV[0];
open(F,$file) || die "Can't open $file: $!";

my $stash;
read F,$stash,1024;

my @unstash=map { $_^0xf5 } unpack("C*",$stash);

foreach my $c (@unstash) {
   last if $c eq 0;
   printf "%c",$c;
}