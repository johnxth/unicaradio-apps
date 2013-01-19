//
//  SongRequestViewControllerViewController.h
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LoadingDialog.h"

#import "US2ValidatorUIDelegate.h"
#import "US2ValidatorUIProtocol.h"

#import "TooltipView.h"
#import "FormTableViewCell.h"

#define kPlistname @"Information.plist"

@interface SongRequestViewController : UIViewController<US2ValidatorUIDelegate,
														FormTableViewCellDelegate,
														UITextFieldDelegate,
														UITextViewDelegate,
														UITableViewDelegate,
														UITableViewDataSource>
{
@private
	LoadingDialog *dialog;

	NSOperationQueue *queue;
	NSTimer *timer;

	TooltipView    *_tooltipView;
    id <US2ValidatorUIProtocol> _tooltipConnectedTextUI;

	NSMutableArray *textFields;

	IBOutlet UITableView *tableView;
}

@property (strong, nonatomic) IBOutlet UITableView *tableView;

@end
