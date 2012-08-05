//
//  Day.m
//  Streaming
//
//  Created by Paolo on 05/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "Day.h"

@implementation Day

+ (enum Day) getDayByString: (NSString *)dayString
{
	if([dayString isEqualToString:MONDAY_KEY]) {
		return MONDAY;
	} else if([dayString isEqualToString:TUESDAY_KEY]) {
		return TUESDAY;
	} else if([dayString isEqualToString:WEDNESDAY_KEY]) {
		return WEDNESDAY;
	} else if([dayString isEqualToString:THURSDAY_KEY]) {
		return THURSDAY;
	} else if([dayString isEqualToString:FRIDAY_KEY]) {
		return FRIDAY;
	} else if([dayString isEqualToString:SATURDAY_KEY]) {
		return SATURDAY;
	} else if([dayString isEqualToString:SUNDAY_KEY]) {
		return SUNDAY;
	} else {
		return MONDAY;
	}
}

+ (NSString *) getStringByDay: (enum Day)day
{
	switch(day) {
		case MONDAY:
			return MONDAY_KEY;
			break;
		case TUESDAY:
			return TUESDAY_KEY;
			break;
		case WEDNESDAY:
			return WEDNESDAY_KEY;
			break;
		case THURSDAY:
			return THURSDAY_KEY;
			break;
		case FRIDAY:
			return FRIDAY_KEY;
			break;
		case SATURDAY:
			return SATURDAY_KEY;
			break;
		case SUNDAY:
			return SUNDAY_KEY;
			break;
		default:
			break;
	}
}

@end
