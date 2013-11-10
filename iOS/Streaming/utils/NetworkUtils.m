//
//  NetworkUtils.m
//  Streaming
//
//  Created by Paolo on 18/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "NetworkUtils.h"
#import "SettingsManager.h"

#import "../libs/reachability/Reachability.h"

@implementation NetworkUtils

+ (NSData *) httpGet:(NSURL *)url
{
	NSURLRequest *request = [NSURLRequest requestWithURL:url
								cachePolicy:NSURLRequestReloadIgnoringLocalAndRemoteCacheData
								timeoutInterval:TIMEOUT_CONNECTION];

	NSURLResponse *response = nil;
	NSError *error = nil;
	NSData *data = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];

	return data;
}

+ (NSData *) httpPost:(NSURL *)url postData:(NSString *)postData contentType:(NSString *)contentType
{
	NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url
										cachePolicy:NSURLRequestReloadIgnoringLocalAndRemoteCacheData
										timeoutInterval:TIMEOUT_CONNECTION];
	[request addValue:contentType forHTTPHeaderField:@"Content-Type"];
	[request setHTTPMethod:@"POST"];
    [request setHTTPBody:[postData dataUsingEncoding:NSUTF8StringEncoding]];

    NSURLResponse *response = nil;
	NSError *error = nil;
	NSData *data = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
	if(error) {
		return nil;
	}

	return data;
}

+ (BOOL) isConnected
{
	Reachability *reachability = [Reachability reachabilityForInternetConnection];
	NetworkStatus networkStatus = [reachability currentReachabilityStatus];

	switch(networkStatus) {
		case NotReachable:
			return NO;
		case ReachableViaWiFi:
			return YES;
		case ReachableViaWWAN:
			return YES;
		default:
			return NO;
	}
}

+ (BOOL) isConnectedToWiFi
{
	Reachability *reachability = [Reachability reachabilityForInternetConnection];
	NetworkStatus networkStatus = [reachability currentReachabilityStatus];
	
	switch(networkStatus) {
		case NotReachable:
			return NO;
		case ReachableViaWiFi:
			return YES;
		case ReachableViaWWAN:
			return NO;
		default:
			return NO;
	}
}

+ (BOOL) isConnectionOK:(SettingsManager *) settingsManager
{
	if(![NetworkUtils isConnected]) {
		return NO;
	}

	NetworkType enabledNetworkType = [settingsManager getNetworkType];
	if(enabledNetworkType == WIFI_ONLY && ![NetworkUtils isConnectedToWiFi]) {
		return NO;
	}

	return YES;
}

+ (BOOL) isConnectionOKForGui:(SettingsManager *) settingsManager
{
	if(![NetworkUtils isConnected]) {
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle: NSLocalizedString(@"NOT_CONNECTED_ALERT_TITLE", @"")
														message: NSLocalizedString(@"NOT_CONNECTED_ALERT_MESSAGE", @"")
													   delegate: nil
											  cancelButtonTitle: @"OK"
											  otherButtonTitles: nil];
		[alert show];

		return NO;
	}

	NetworkType enabledNetworkType = [settingsManager getNetworkType];
	if(enabledNetworkType == WIFI_ONLY && ![NetworkUtils isConnectedToWiFi]) {
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle: NSLocalizedString(@"NOT_IN_WIFI_ALERT_TITLE", @"")
														message: NSLocalizedString(@"NOT_IN_WIFI_ALERT_MESSAGE", @"")
													   delegate: nil
											  cancelButtonTitle: @"OK"
											  otherButtonTitles: nil];
		[alert show];

		return NO;
	}

	return YES;
}

@end
