//
//  Schedule.h
//  Streaming
//
//  Created by Paolo on 05/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "../enums/Day.h"

@interface Schedule : NSObject
{
	NSMutableDictionary *transmissions;
}

@property (nonatomic, retain) NSMutableDictionary *transmissions;

+ (Schedule *) fromJSON: (NSData *)json;
- (NSArray *) getTransmissionsByDay: (enum Day)day;

@end
