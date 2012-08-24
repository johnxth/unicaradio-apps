//
//  RequestSongOperation.m
//  Streaming
//
//  Created by Paolo on 20/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "RequestSongOperation.h"
#import "../utils/NetworkUtils.h"
#import "../JSONKit/JSONKit.h"
#import "../enums/Error.h"

@implementation RequestSongOperation

- (id) initWithSongRequest:(SongRequest *)songRequest
{
	if(self = [super init]) {
		request = songRequest;
	}
	
	return self;
}

- (void) main
{
	NSDictionary *requestDictionary = [NSDictionary dictionaryWithObjectsAndKeys: @"sendEmail", @"method", [request toJSON], @"params", nil];
	
    NSString *jsonRequest = [requestDictionary JSONString];
	NSLog(@"%@", jsonRequest);
	NSURL *url = [NSURL URLWithString:WEB_SERVICE];
	NSData *result = [NetworkUtils httpPost:url postData:jsonRequest contentType:@"application/json"];
	if(result == nil) {
		NSMutableDictionary *errorDict = [NSMutableDictionary dictionary];
		[errorDict setObject:[NSNumber numberWithInt:INTERNAL_DOWNLOAD_ERROR] forKey:@"errorCode"];
		[[NSNotificationCenter defaultCenter] postNotificationName:@"SendEmail" object:errorDict];
		return;
	}

	NSMutableDictionary *resultsDictionary = [result objectFromJSONData];
	[[NSNotificationCenter defaultCenter] postNotificationName:@"SendEmail" object:resultsDictionary];
}

@end
