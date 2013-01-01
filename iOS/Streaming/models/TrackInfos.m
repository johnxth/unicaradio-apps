//
//  TrackInfos.m
//  Streaming
//
//  Created by Paolo on 31/12/12.
//
//

#import "TrackInfos.h"

@implementation TrackInfos

@synthesize author;
@synthesize title;
@synthesize cover;

- (id) init
{
	self = [super init];
	if(self) {
		[self clean];
	}

	return self;
}

- (void) clean
{
	[self setAuthor:nil];
	[self setTitle:nil];
	[self setCover:nil];
}

- (void) setTrackInfos:(TrackInfos *)infos
{
	[self setAuthor:infos.author];
	[self setTitle:infos.title];
	[self setCover:infos.cover];
}

- (BOOL) isClean
{
	return author == nil && title == nil && cover == nil;
}

@end
