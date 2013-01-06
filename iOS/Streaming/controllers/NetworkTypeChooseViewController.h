//
//  NetworkTypeChooseViewController.h
//  Streaming
//
//  Created by Paolo on 03/01/13.
//
//

#import <UIKit/UIKit.h>
#include "../enums/NetworkType.h"
#include "../enums/Preferences.h"

@protocol NetworkTypeChooseViewControllerDelegate

- (void)networkTypeChangedForPreference:(Preferences)preference andNetworkType:(NetworkType)selectedNetworkType;

@end

@interface NetworkTypeChooseViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>
{
	id<NetworkTypeChooseViewControllerDelegate> delegate;
	Preferences preference;
	NetworkType currentNetworkType;

	UITableViewCell *selectedTableViewCell;
}

@property(nonatomic, retain) id<NetworkTypeChooseViewControllerDelegate> delegate;

- (id)initWithPreference:(Preferences)preference andCurrentValue:(NetworkType)value;

@end
