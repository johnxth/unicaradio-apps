//
//  Schedule.m
//  Streaming
//
//  Created by Paolo on 05/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "Schedule.h"

#import "../libs/JSONKit/JSONKit.h"
#import "Transmission.h"

@implementation Schedule

@synthesize transmissions;

- (id) init
{
	if (self = [super init])
    {
		self.transmissions = [[NSMutableDictionary alloc] init];
		for(int day = MONDAY; day <= SUNDAY; day++) {
			NSString *dayString = [Day getStringByDay:day];

			NSMutableArray *array = [[NSMutableArray alloc] init];
			[self.transmissions setObject:array forKey:dayString];
		}
    }

    return self;
}

+ (Schedule *) fromJSON: (NSData *)json
{
	Schedule *result = [[Schedule alloc] init];

	NSMutableDictionary *resultsDictionary = [json objectFromJSONData];
	for(int day = MONDAY; day <= SUNDAY; day++) {
		NSString *dayString = [Day getStringByDay:day];
		NSMutableArray *transmissionsByDay = [result getTransmissionsByDay:day];

		NSArray *itemArray = [resultsDictionary objectForKey:dayString];
		for (NSMutableDictionary *transmissionDictionary in itemArray) {
			NSString *formatName = [transmissionDictionary objectForKey:PROGRAM_KEY];
			NSString *startTime = [transmissionDictionary objectForKey:START_TIME_KEY];
			Transmission *transmission = [[Transmission alloc] initWithFormatName:formatName andStartTime:startTime];
			[transmissionsByDay addObject:transmission];
		}

		NSMutableArray *sortedTransmissions = [Transmission sortTransmissions:transmissionsByDay];
		[result.transmissions setObject:sortedTransmissions forKey:dayString];
	}

	return result;
}

- (NSArray *) getTransmissionsByDay: (enum Day)day
{
	NSString *dayString = [Day getStringByDay:day];
	return [self.transmissions objectForKey:dayString];
}

@end
