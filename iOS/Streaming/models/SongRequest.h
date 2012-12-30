//
//  SongRequest.h
//  Streaming
//
//  Created by Paolo on 20/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

#define IOS_APP_CODE 0x00E6

@interface SongRequest : NSObject
{
	NSString *author;
	NSString *title;
	NSString *email;
}

@property (nonatomic, strong) NSString *author;
@property (nonatomic, strong) NSString *title;
@property (nonatomic, strong) NSString *email;

- (id) init;
- (NSDictionary *)toJSON;
- (NSString *)toJSONString;

@end
