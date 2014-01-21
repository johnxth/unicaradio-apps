//
//  SettingsManager.h
//  Streaming
//
//  Created by Paolo on 09/01/13.
//
//

#import <Foundation/Foundation.h>

#import "../enums/NetworkType.h"

#define SETTINGS_PLIST @"Settings.plist"

#define PREF_NETWORK_TYPE @"PREF_NETWORK_TYPE"
#define PREF_COVER_NETWORK @"PREF_COVER_NETWORK"
#define PREF_INSTALLED_VERSION @"PREF_INSTALLED_VERSION"

@interface SettingsManager : NSObject
{
	NSMutableDictionary *settings;
}

@property (strong, nonatomic) NSDictionary *settings;

+ (id) getInstance;

- (id) getPreference:(NSString *) preference;
- (void) savePreference:(NSString *) preference andValue:(id) value;

- (NetworkType) getNetworkType;
- (NetworkType) getNetworkTypeForCover;
- (int) getInstalledVersion;

- (void) saveNetworkType:(NetworkType) networkType;
- (void) saveNetworkTypeForCover:(NetworkType) networkType;
- (void) updateInstalledVersion;

@end
