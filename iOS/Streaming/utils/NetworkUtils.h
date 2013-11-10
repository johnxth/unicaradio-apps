//
//  NetworkUtils.h
//  Streaming
//
//  Created by Paolo on 18/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

#define TIMEOUT_CONNECTION 60.0

@class SettingsManager;

@interface NetworkUtils : NSObject
{
	
}

+ (NSData *) httpGet: (NSURL *)url;

+ (NSData *) httpPost: (NSURL *)url postData:(NSString *)postData contentType:(NSString *)contentType;

+ (BOOL) isConnected;

+ (BOOL) isConnectedToWiFi;

+ (BOOL) isConnectionOKForGui:(SettingsManager *) settingsManager;

@end
