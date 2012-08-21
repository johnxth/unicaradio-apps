//
//  RequestSongOperation.h
//  Streaming
//
//  Created by Paolo on 20/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "../models/SongRequest.h"

#define WEB_SERVICE @"http://www.unicaradio.it/regia/test/unicaradio-mail/endpoint.php"

@interface RequestSongOperation : NSOperation
{
	SongRequest *request;
}

- (id) initWithSongRequest: (SongRequest *)songRequest;

@end
